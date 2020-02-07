package com.kurtisisenad.hotelmanager.classes;

/**
 *
 * @author Senad Kurtisi
 */
public class Employee {
    public static float beginnerPay = 65000;
    private String firstName;
    private String lastName;
    private String birthDate;
    String gender;
    private int age;
    private float pay;
    private String street;
    private String phoneNumber;
    private int level;
    
    public Employee(String fName, String lName, int theAge, String dateOfBirth,
                    String theStreet, String thePhoneNumber, String theGender,
                    int theLevel){
        firstName = fName;
        lastName = lName;
        birthDate = dateOfBirth;
        gender = theGender;
        age = theAge;
        pay = beginnerPay;
        street = theStreet;
        phoneNumber = thePhoneNumber;
        level = theLevel;
    }
    
    public String getFirstName(){
        return firstName;
    }
    
    public String getlastName(){
        return lastName;
    }
    
    public String getName(){
        return (firstName + " " + lastName);
    }
    
    public String getBirthDate(){
        return birthDate;
    }
    
    public String getGender(){
        return gender;
    }
    
    public int getAge(){
        return age;
    }
    
    public float getPay(){
        return pay;
    }
    
    public String getStreet(){
        return street;
    }
    
    public String getPhoneNumber(){
        return phoneNumber;
    }
    
    public int getLevel(){
        return level;
    }
}
