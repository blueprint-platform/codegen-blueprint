# Compiled source #
###################
*.com
*.class
*.dll
*.exe
*.o
*.so

# Packages #
############
# it's better to unpack these files and commit the raw source
# git has its own built in compression methods
*.7z
*.dmg
*.gz
*.iso
*.jar
*.rar
*.tar

# Logs and databases #
######################
*.log
*.sql
*.sqlite

# OS generated files #
######################
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db
../.DS_Store


.classpath
.project
.settings/
target/
/build/
/bin/


# IDE specific files and folders
.idea/
.project
.classpath
.settings/
*.iws
*.iml
*.ipr


# Maven specific files and folders
**/target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
pom.xml.bak
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/

# Generated source folders (common choices)
target/
generated-sources/
generated-classes/

# Add placeholders for project-specific ignores (optional)
<#if ignoreList?has_content && ignoreList?size gt 0>
    <#list ignoreList as pattern>
        ${pattern}
    </#list>
</#if>
