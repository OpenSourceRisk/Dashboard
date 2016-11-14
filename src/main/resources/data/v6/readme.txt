
The data files are packaged into the jar file.
In source code we want to do dir listings on the data.

This is not supported in java, you can only retrieve a resource
if you already know its name.

Our solution is the file dirlist.txt which is encapsulated by
class app.load.FileList to manage access to the files.

Every time you change the contents of this directory or its
subdirectories, you need to regenerate the dirlist.txt file.

cd into the grandparent directory (resources) and do:

    find data/v6 > data/v6/dirlist.txt

