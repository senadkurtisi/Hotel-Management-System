# Hotel Manager

## Project Tree and How To Run It
Uploaded files represent NetBeans project .
Project folder contains folder ***EXECUTABLE***
which contains executable jar and all .txt
necessary resources. Originally the folder was called
***dist** after NetBeans clean&build but was renamed
for simplicity.

Also project can be opened in the NetBeans.


## Built with
- [NetBeans 8.2](https://netbeans.org/)
- [Java SE Development Kit 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [MySQL Workbench](https://www.mysql.com/products/workbench/)


## Project description
**Hotel Manager** is a hotel management software
which can help hotel employees easily manage
everyday situations.

### Employee distinction
There are three levels of employees in the hotel.
1. Regular
2. Manager
3. Owner

#### Regular
Regular employees can only check in new guests,
check out current guests, make an order for a
specific room(current guest) or search for 
information about specific room. 
**Currently there is one regular employee.**
**username: 'mark-johnson'**
**password: 'mark johnson'**

#### Manager
Managers can do everything regular employee can
with addition to editing information about regular
employees. For example: name change, picture change,
raise etc. Managers can't edit other managers or
owners, of course.
**Currently there is one manager.**
**username: 'jane-johnson'**
**password: 'jane johnson'**

#### Owner
Owners of the hotel can do everything regular employees
and managers can, with possibility to edit manager
information or delete managers. 
Owners can also add new employees. 
**Owner log in credentials:**
**username: 'admin-admin'**
**password: 'adminpassword'**


## Project components

### Log In Form
The GUI of the software primarily contains log in
form which is being shown by starting .jar file
***"Hotel manager"*** from the ***"EXECUTABLE"***
directory.

Log in form requests employee's log in credentials
which must be valid.. 
.sql file of the database can be found at the
location: ***"src/hotel_manager_db.sql"***. That file
can be loaded to the MySQL Workbench and edited freely.
**MySQL Workbench localhost credentials:**
**username: root**
**password: adminroot**

### Hotel Manager Form
Hotel Manager form contains three tabs:
- Booking Management
- Menu
- Employees Management

#### Booking Management
##### Guest Check In
In the Booking Management list of all hotel's 
accommodation sections(rooms/apartments) is being 
shown. 
Currently logged in employee can check in new guest
by choosing room number and entering guests info. 

##### Room search
Currently logged in employee can search for information
about guest that currently stays in specified room.

##### Guest Check Out
Currently logged in employee can check out guest from
specified room.

#### Menu
In the menu tab a list of all possible items from the 
menu is being shown.

Currently logged in employee can update the receipt for a
specific room.

#### Employee Management
Currently logged in employee (Manager/Owner) can search for
information about employee by his ID. Both managers and owners
can edit those information and update the database or delete
employees, but managers can't edit or delete other managers, 
and of course, owners. Owners can edit and delete everyone.

###### New employee
Owners can add new employees, by specifying all necessary information.
Every field must be filled and correctly formatted in order to add 
new employee. If not warning message will pop up. Also, employee level
can be specified. When new employee is created we get notification
about the ID of the recently created employee.
