package com.example.a2m.rbt.Utilities;

public class user {
    public  String id =null;
    public String firstName=null;
    public String lastName=null;
    public String email=null;
    public String password=null;
    public String contactNumber=null;
    public String address=null;
    public String dateOfBirth=null;
    public String typeOfUser;
    public String nationalNumber=null;
    public String driverUsername=null;

    public user() {
    }

    public user(String id,String firstName, String lastName, String email, String password, String contactNumber, String address, String dateOfBirth, String typeOfUser, String nationalNumber , String driverUsername) {
        this.id=id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.typeOfUser = typeOfUser;
        this.nationalNumber = nationalNumber;
        this.driverUsername=driverUsername;
    }
}
