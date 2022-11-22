package [=repositoryPkg];

import [=modelPkg].[=modelName];
import org.springframework.data.repository.CrudRepository;

public interface [=repositoryName] extends CrudRepository<[=modelName], Long>, [=modelName]Jdbc {
}