Write-Output $PSScriptRoot

. "$PSScriptRoot\jre\bin\java.exe" -cp "$PSScriptRoot\stockerfx" com.mahara.stocker.AppStarter

"按回车键退出"  ;
Read-Host | Out-Null ;
Exit