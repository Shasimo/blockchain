# info-f308 : Projet blockchain
## Software
### Android Studio
A fork of intellij with a drag and drop designing tool
### GitHub Desktop
Github hassle free with a beautiful gui integration
## Libraries 
### Gson  
A json serialisation/deserialisation library
> there is a lot of documentation on the [github](https://github.com/google/gson) page   
## Reminder about git 
Generally you won't have to use the command line but it might be handy for small and fast editing. If you want to use branches **please do not attemps to use the command line**

### General commands
```SHELL
$ git status
```
a handy command to check if you have to pull, which files have been modified, ...

### Dowloading other work
```SHELL
$ git pull
```
will pull the main branch to your local repository 
```SHELL
$ git discard <pathspec>...
```
will discard the changes you have made to the specified files 
### Uploading your work
**Do not forget** to pull before doing the next steps
```SHELL
$ git add <pathspec>... 
```
stages the content for the next commit
```SHELL
$ git commit -m <msg>
``` 
gets all the staged changes ready to be pushed (stay short with your description)
```SHELL
$ git push
```   
if there is an error **please do not use the force option**, try to resolve it by doing a merge with a tool like GitHub Dekstop, VsCode git lens addon, ...  
> if you are really stuck please ask for help on discord