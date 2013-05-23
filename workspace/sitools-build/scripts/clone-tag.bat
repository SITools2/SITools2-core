::%1 correspond à l'url du dépot 
::%2 correspond au dossier destination du clone
::%3 correspond au nom du tag à récupérer
git.exe clone %1 %2
cd %2
git.exe checkout %3
