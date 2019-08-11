package com.nconnect.teacher.model.chat;

public class Parents {
    private Mother mother;

    private String student_name;

    private String student_id;

    private Father father;

    public void setMother(Mother mother){
        this.mother = mother;
    }
    public Mother getMother(){
        return this.mother;
    }
    public void setStudent_name(String student_name){
        this.student_name = student_name;
    }
    public String getStudent_name(){
        return this.student_name;
    }
    public void setStudent_id(String student_id){
        this.student_id = student_id;
    }
    public String getStudent_id(){
        return this.student_id;
    }
    public void setFather(Father father){
        this.father = father;
    }
    public Father getFather(){
        return this.father;
    }
}