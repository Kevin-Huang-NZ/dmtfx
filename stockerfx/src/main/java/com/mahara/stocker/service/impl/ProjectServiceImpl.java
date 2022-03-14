package com.mahara.stocker.service.impl;

import com.mahara.stocker.dao.*;
import com.mahara.stocker.model.CommonWord;
import com.mahara.stocker.model.Project;
import com.mahara.stocker.model.Roman;
import com.mahara.stocker.model.Transliteration;
import com.mahara.stocker.service.ProjectService;
import com.mahara.stocker.util.PaginationIn;
import org.ahocorasick.trie.PayloadEmit;
import org.ahocorasick.trie.PayloadTrie;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PlaceNameRepository placeNameRepository;
    @Autowired
    private TransliterationRepository transliterationRepository;
    @Autowired
    private CommonWordRepository commonWordRepository;
    @Autowired
    private RomanRepository romanRepository;
    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Override
    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
        placeNameRepository.deleteByProject(id);
    }

    @Override
    public void translate(Project project) {
        var transliterations = transliterationRepository.findByStandard(project.getStandardId());
        var commonWords = commonWordRepository.findByStandard(project.getStandardId());
        var romans = romanRepository.findByStandard(project.getStandardId());
        if (romans != null && romans.size() > 0) {
            execRoman(project.getId(), romans);
        }

        if (transliterations != null && transliterations.size() > 0) {
            execTrans(project.getId(), transliterations, commonWords);
        }
//        var transactionStatus = transactionManager.getTransaction(null);
    }

    public void execRoman(Long projectId, List<Roman> romans){
        var pageNo = 1;
        var pageSize = 10000;
        var searchResult = placeNameRepository.findByProject(projectId, new PaginationIn(pageNo, pageSize));

        var romanConverter = new RomanConverter(romans);
        do {
            if (searchResult.getData() != null && !searchResult.getData().isEmpty()) {
                var converted = searchResult.getData().stream().map(p -> {
                    var tmp = romanConverter.convert(p.getOriginal());
                    p.setRoman(tmp);
                    return p;
                }).collect(Collectors.toList());

                placeNameRepository.batchUpdateRoman(converted);
            }
            pageNo++;
            searchResult = placeNameRepository.findByProject(projectId, new PaginationIn(pageNo, pageSize));
        } while (searchResult.getData() != null && !searchResult.getData().isEmpty());
    }

    public void execTrans(Long projectId, List<Transliteration> transliterations, List<CommonWord> commonWords) {
        var pageNo = 1;
        var pageSize = 10000;
        var searchResult = placeNameRepository.findByProject(projectId, new PaginationIn(pageNo, pageSize));

        var translator = new Translator(transliterations, commonWords);
        do {
            if (searchResult.getData() != null && !searchResult.getData().isEmpty()) {
                var converted = searchResult.getData().stream().map(p -> {
                    var tmp = translator.convert(p.getOriginal());
                    if (tmp != null) {
                        p.setTransliteration(tmp.getTransliteration());
                        p.setFreeTranslation(tmp.getFreeTranslation());
                        p.setEmitStandard(tmp.getEmitStandard());
                    }
                    return p;
                }).collect(Collectors.toList());

                placeNameRepository.batchUpdateAutoTrans(converted);
            }
            pageNo++;
            searchResult = placeNameRepository.findByProject(projectId, new PaginationIn(pageNo, pageSize));
        } while (searchResult.getData() != null && !searchResult.getData().isEmpty());

    }

    class RomanConverter {
        private Map<String, String> romanMap = new HashMap<String, String>();

        public RomanConverter(List<Roman> romans) {
            romans.forEach(r -> this.romanMap.put(r.getOriginalAlpha(), r.getRomanAlpha()));
        }

        public String convert(String originalString) {
            if (StringUtils.isEmpty(originalString)) {
                return originalString;
            }
            Character[] originalChars = ArrayUtils.toObject(originalString.toCharArray());
            return Arrays.stream(originalChars).map(temp -> {
                String p = String.valueOf(temp);
                String v = romanMap.get(p);
                if (StringUtils.isEmpty(v)) {
                    return p;
                } else {
                    return v;
                }
            }).collect(Collectors.joining());
        }
    }


    class Translator {
        private static final String PREFIX = "^^";
        private static final String POSTFIX = "$$";
        // 为了处理4-前置（在xxx之前）；5-后置（在xxx之后）
        // 建立两个字典树：
        //     第1个：以原始的原文字段为作为keyword。
        //     第2个：针对4/5两个匹配方式，将匹配参数和原文字段组合，作为keyword
        private PayloadTrie<TranslateRule> trie = null;
        private PayloadTrie<TranslateRule> trieComposed = null;

        public Translator(List<Transliteration> transliterations, List<CommonWord> commonWords) {
            var tb = PayloadTrie.<TranslateRule>builder().ignoreCase().ignoreOverlaps();
            var tbComposed = PayloadTrie.<TranslateRule>builder().ignoreCase().ignoreOverlaps();
            transliterations.forEach(t -> {
                if (StringUtils.equals(t.getMatchWay(), "4") || StringUtils.equals(t.getMatchWay(), "5")) {
                    if (StringUtils.isEmpty(t.getMatchParams())) {
                        tbComposed.addKeyword(t.getOriginal(), new TranslateRule(t.getOriginal(), "0", t.getMatchWay(), t.getMatchParams(), t.getChinese(), ""));
                    } else {
                        var params = t.getMatchParams();
                        params = params.replaceAll("；", ";");
                        for (var param : params.split(";")) {
                            if (StringUtils.equals(t.getMatchWay(), "4")) {
                                tbComposed.addKeyword(t.getOriginal()+param, new TranslateRule(t.getOriginal(), "0", t.getMatchWay(), param, t.getChinese(), ""));
                            } else if (StringUtils.equals(t.getMatchWay(), "5")) {
                                tbComposed.addKeyword(param + t.getOriginal(), new TranslateRule(t.getOriginal(), "0", t.getMatchWay(),param, t.getChinese(), ""));
                            }
                        }
                    }
                    tb.addKeyword(t.getOriginal(), new TranslateRule(t.getOriginal(), "0", t.getMatchWay(), t.getMatchParams(), t.getChinese(), ""));
                } else {
                    var original = t.getOriginal();
                    // 针对2-前缀（词头）；3-后缀（词尾），将原文加工后作为keyword
                    if (StringUtils.equals(t.getMatchWay(), "2")) {
                        original = PREFIX + original;
                    } else if (StringUtils.equals(t.getMatchWay(), "3")) {
                        original = original + POSTFIX;
                    }
                    tb.addKeyword(original, new TranslateRule(t.getOriginal(), "0", t.getMatchWay(), t.getMatchParams(), t.getChinese(), ""));
                    tbComposed.addKeyword(original, new TranslateRule(t.getOriginal(), "0", t.getMatchWay(), t.getMatchParams(), t.getChinese(), ""));
                }
            });
            commonWords.forEach(t -> {
                if (StringUtils.equals(t.getMatchWay(), "4") || StringUtils.equals(t.getMatchWay(), "5")) {
                    if (StringUtils.isEmpty(t.getMatchParams())) {
                        tbComposed.addKeyword(t.getOriginal(), new TranslateRule(t.getOriginal(), t.getOriginalType(), t.getMatchWay(), t.getMatchParams(), t.getTransliteration(), t.getFreeTranslation()));
                    } else {
                        var params = t.getMatchParams();
                        params = params.replaceAll("；", ";");
                        for (var param : params.split(";")) {
                            if (StringUtils.equals(t.getMatchWay(), "4")) {
                                tbComposed.addKeyword(t.getOriginal()+param, new TranslateRule(t.getOriginal(), t.getOriginalType(), t.getMatchWay(), param, t.getTransliteration(), t.getFreeTranslation()));
                            } else if (StringUtils.equals(t.getMatchWay(), "5")) {
                                tbComposed.addKeyword(param + t.getOriginal(), new TranslateRule(t.getOriginal(), t.getOriginalType(), t.getMatchWay(), param, t.getTransliteration(), t.getFreeTranslation()));
                            }
                        }
                    }
                    tb.addKeyword(t.getOriginal(), new TranslateRule(t.getOriginal(), t.getOriginalType(), t.getMatchWay(), t.getMatchParams(), t.getTransliteration(), t.getFreeTranslation()));
                } else {
                    var original = t.getOriginal();
                    // 针对2-前缀（词头）；3-后缀（词尾），将原文加工后作为keyword
                    if (StringUtils.equals(t.getMatchWay(), "2")) {
                        original = PREFIX + original;
                    } else if (StringUtils.equals(t.getMatchWay(), "3")) {
                        original = original + POSTFIX;
                    }
                    tb.addKeyword(original, new TranslateRule(t.getOriginal(), t.getOriginalType(), t.getMatchWay(), t.getMatchParams(), t.getTransliteration(), t.getFreeTranslation()));
                    tbComposed.addKeyword(original, new TranslateRule(t.getOriginal(), t.getOriginalType(), t.getMatchWay(), t.getMatchParams(), t.getTransliteration(), t.getFreeTranslation()));
                }
            });
            trie = tb.build();
            trieComposed = tbComposed.build();
        }

        public TransResult convert(String originalString) {
            if (StringUtils.isEmpty(originalString)) {
                return null;
            }
            TransResult transResult = null;
            if (this.trie != null) {
                originalString = originalString.toLowerCase();
                // 为了能够触发2-前缀（词头）；3-后缀（词尾），给原文加上前后缀
                originalString = PREFIX + originalString + POSTFIX;
                var emits = trie.parseText(originalString);
                // 判断是否触发了4-前置（在xxx之前）；5-后置（在xxx之后）
                var rules45 = new ArrayList<TranslateRule>();
                emits.forEach(pe -> {
                    var tr = pe.getPayload();
                    if (StringUtils.equals(tr.getMatchWay(), "4") || StringUtils.equals(tr.getMatchWay(), "5")) {
                        rules45.add(tr);
                    }
                });
                if (!rules45.isEmpty()) {
                    // 触发了4-前置（在xxx之前）；5-后置（在xxx之后）
                    var replaceKey = new HashMap<String, String>();
                    String finalOriginalString = originalString;
                    rules45.forEach(t -> {
                        var params = t.getMatchParams();
                        if (!StringUtils.isEmpty(params)) {
                            params = params.replaceAll("；", ";");
                            for (var param : params.split(";")) {
                                if (StringUtils.equals(t.getMatchWay(), "4")) {
                                    if (finalOriginalString.indexOf(t.getOriginal()+param) >= 0) {
                                        replaceKey.put(t.getOriginal() + param, t.getOriginal() + param + param);
                                    }
                                } else if (StringUtils.equals(t.getMatchWay(), "5")) {
                                    if (finalOriginalString.indexOf(param + t.getOriginal()) >= 0) {
                                        replaceKey.put(param + t.getOriginal(), param + param + t.getOriginal());
                                    }
                                }
                            }
                        }
                    });
                    var newOriginal = originalString;
                    for (var entry : replaceKey.entrySet()) {
                        newOriginal = newOriginal.replace(entry.getKey(), entry.getValue());
                    }
                    emits = trieComposed.parseText(originalString);
                }
                // 没有触发4-前置（在xxx之前）；5-后置（在xxx之后）
                var tArr = new ArrayList<String>();
                var ftArr = new ArrayList<String>();
                var emitStandards = new ArrayList<String>();
                emits.forEach(pe -> {
                    var tr = pe.getPayload();
                    tArr.add(tr.getTransliteration());
                    if (StringUtils.isEmpty(tr.getFreeTranslation())) {
                        ftArr.add(tr.getTransliteration());
                    } else {
                        ftArr.add(tr.getFreeTranslation());
                    }
                    emitStandards.add(tr.toString());
                });
                transResult = new TransResult();
                var transliteration = removePrefixPostfix(String.join("", tArr));
                var freeTranslation = removePrefixPostfix(String.join("", ftArr));

                transResult.setTransliteration(transliteration);
                if (!StringUtils.equals(transliteration, freeTranslation)) {
                    transResult.setFreeTranslation(freeTranslation);
                }
                transResult.setEmitStandard(String.join("", emitStandards));

            }

            return transResult;
        }

        private String removePrefixPostfix(String translated) {
            if (StringUtils.isEmpty(translated)) {
                return translated;
            }
            var tmp = translated;
            if(tmp.startsWith(PREFIX)) {
                tmp = tmp.substring(2);
            }
            if (tmp.endsWith(POSTFIX)) {
                tmp = tmp.substring(0, tmp.length()-2);
            }
            return tmp;
        }
    }

    class TranslateRule {
        private String original;
        // 0-音译表；1-人名；2-通名；3-形容词；x-其它
        private String originalType;
        // 匹配方式：1-精确；2-前缀（词头）；3-后缀（词尾）；4-前置（在xxx之前）；5-后置（在xxx之后）
        private String matchWay;
        private String matchParams;
        private String transliteration;
        private String freeTranslation;
        private Map<String, String> originalTypeMap = new HashMap<>(5){{
            put("0", "音译表");
            put("1", "人名");
            put("2", "通名");
            put("3", "形容词");
            put("x", "其它");
        }};
        private Map<String, String> matchWayMap = new HashMap<>(5){{
            put("1", "精确匹配");
            put("2", "前缀匹配（词头）");
            put("3", "后缀匹配（词尾）");
            put("4", "前置匹配（在xxx之前）");
            put("5", "后置匹配（在xxx之后）");
        }};

        public TranslateRule(String original, String originalType, String matchWay, String matchParams, String transliteration, String freeTranslation) {
            this.original = StringUtils.defaultString(original);
            this.originalType = StringUtils.defaultString(originalType);
            this.matchWay = StringUtils.defaultString(matchWay);
            this.matchParams = StringUtils.defaultString(matchParams);
            this.transliteration = StringUtils.defaultString(transliteration);
            this.freeTranslation = StringUtils.defaultString(freeTranslation);
        }

        public String getOriginal() {
            return original;
        }
        public String getOriginalType() {
            return originalType;
        }
        public String getMatchWay() {
            return matchWay;
        }
        public String getMatchParams() {
            return matchParams;
        }
        public String getTransliteration() {
            return transliteration;
        }
        public String getFreeTranslation() {
            return freeTranslation;
        }

        @Override
        public String toString() {
            var sb = new StringBuilder()
                    .append(originalTypeMap.get(originalType))
                    .append(" - ")
                    .append(original)
                    .append(" - ")
                    .append(matchWayMap.get(matchWay))
                    .append(" - ")
                    .append(matchParams)
                    .append(" => ")
                    .append(transliteration)
                    .append(" / ")
                    .append(freeTranslation)
                    .append(".");

            return sb.toString();
        }
    }

    class TransResult {
        private String transliteration;
        private String freeTranslation;
        private String emitStandard;

        public String getTransliteration() {
            return transliteration;
        }

        public void setTransliteration(String transliteration) {
            this.transliteration = transliteration;
        }

        public String getFreeTranslation() {
            return freeTranslation;
        }

        public void setFreeTranslation(String freeTranslation) {
            this.freeTranslation = freeTranslation;
        }

        public String getEmitStandard() {
            return emitStandard;
        }

        public void setEmitStandard(String emitStandard) {
            this.emitStandard = emitStandard;
        }
    }
}
