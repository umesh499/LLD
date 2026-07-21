package designPatterns;

public class Builder {
    public static void main(String[] args) {
        Student s = new Student.StudentBuilder()
                           .setAddress("Tellapur")
                           .setName("umesh")
                           .setRollNo(1).build();

        System.out.println(s.toString());
    }
    

}


class Student{
    String name;
    int rollNo;
    String address;
    private Student(){

    }
    private Student(StudentBuilder sb){
        this.address = sb.address;
        this.name = sb.name;
        this.rollNo = sb.rollNo;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.name+ " "+ this.rollNo +" "+ this.address;
    }
    static class  StudentBuilder{
        String name;
        int rollNo;
        String address;
        StudentBuilder(){

        }
        public StudentBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public StudentBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public StudentBuilder setRollNo(int rollNo) {
            this.rollNo = rollNo;
            return this;
        }

        public Student build(){
            Student s = new Student(this);
            return s;
        }
    }
}
