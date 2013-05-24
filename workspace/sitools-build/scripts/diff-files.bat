:: %1 correspond au commit d'origine
:: %2 correspond au commit de fin
:: %3 correspond au dossier destination du clone

cd %3

:: generation des fichiers
git.exe diff --name-only --diff-filter=MCR %1 %2 > modified_list-diff.txt
git.exe diff --name-only --diff-filter=A %1 %2 > added_list-diff.txt
git.exe diff --name-only --diff-filter=D  %1 %2 > deleted_list-diff.txt

:: tri des fichiers
java -jar lib/FileSorting.jar modified_list-diff.txt modified_list-diff.txt
java -jar lib/FileSorting.jar added_list-diff.txt added_list-diff.txt
java -jar lib/FileSorting.jar deleted_list-diff.txt deleted_list-diff.txt

git.exe ls-tree -r --name-only %2 > list-file.txt