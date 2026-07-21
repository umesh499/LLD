package designPatterns.structural;

import java.util.ArrayList;
import java.util.List;

public class Composite {
    public static void main(String[] args) {
        IFileSystem fs = new Folder("root");
        fs.addComponent(new File("CodeGen.md", 100));
        IFileSystem fs1 = new Folder("Folder1");
        fs.addComponent(fs1);
        fs1.addComponent(new File("CodeGen111", 20));
        IFileSystem fs2 = new Folder("Folder2");
        fs1.addComponent(fs2);
        fs2.addComponent(new File("codegen222", 30));
        System.out.println(fs.getSize());
    }
}

 interface IFileSystem {
    int getSize();
    void rename(String name);
    void printDetails();
    default void addComponent(IFileSystem fileSystem){
        throw new UnsupportedOperationException();
    }
    default void removeComponent(IFileSystem fileSystem){
        throw new UnsupportedOperationException();
    }
}

class File implements IFileSystem{

    String name;
    int size;
    File(String name, int size){
        this.name=name;
        this.size=size;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void rename(String name) {
       this.name = name;
    }

    @Override
    public void printDetails() {
       System.out.println("Filename :"+this.name);
    }
    
}
class Folder implements IFileSystem {
    String name;
    List<IFileSystem> lst;
    Folder(String name){
        this.name = name;
        lst = new ArrayList<>();
    }
    @Override
    public int getSize() {
        int size = 0;
        for(IFileSystem fs : lst){
            fs.printDetails();
            size+= fs.getSize();
        }
        return size;
    }

    public void addComponent(IFileSystem fileSystem){
        lst.add(fileSystem);
    }

    public void removeComponent(IFileSystem fileSystem){
        lst.remove(fileSystem);
    }

    @Override
    public void rename(String name) {
       this.name = name;
    }

    @Override
    public void printDetails() {
       System.out.println("Foldername :"+this.name);
    }

    
}