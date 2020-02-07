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
Regular employees can only chech in new guests,
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
which must be valid. The validity of credentials is
being checked by searching the locally stored MySQL
database created in **MySQL Workbench**. 
Also, .sql file of the database can be found at the
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
shown along with room number, type, price per nigh
and availability status(Free/Occupied). 
Currently logged in employee can check in new guest
by choosing room number and entering guests first 
and last name. 
**Only rooms that are not occupied can be chosen.**
*When new guest is added .txt file is being saved
with format: 'room-(room_number).txt'. Initial file
contains only the price per night of the room.*

##### Room search
Currently logged in employee can search for information
about guest that currently stays in specified room.
Current room receipt is also displayed.
**Message pops up if room is not occupied.**

##### Guest Check Out
Currently logged in employee can check out guest from
specified room. Message pops up if no room is chosen.
After check out, room txt file is deleted and total 
receipt value along with room and guest information is 
added to the cash-register file.
*Also, a confirmation message pops up for preventing
accidental clicks on the check out button.*

#### Menu
In the menu tab a list of all possible items from the 
menu is being shown with next information: 
- Item name
- Item price
- Item availability

Currently logged in employee can choose an item, number
of desired items, and add it to current receipt session.
Only valid number of items can be added(>0).
After selecting the room, room receipt file is being updated
by adding newly acquired items in format: 
name/amount/price(amount*singular price)
Also, there is confirmation message of the receipt update.

#### Employee Management
Currently logged in employee (Manager/Owner) can search for
information about employee by his ID. Both managers and owners
can edit those information and update the database or delete
employees, but managers can't edit or delete other managers, 
and of course, owners. Owners can edit and delete everyone.
*Employee information that is being shown:*
- First name
- Last name
- Date of birth
- Age
- Pay
- Place of living
- Phone number
- Gender
- Profile picture

***Along with directly editing pay, managers and owners can apply
raise to the selected employee, again with respect to the selected
employee level.***

###### New employee
Owners can add new employees, by specifying all necessary information.
Every field must be filled and correctly formatted in order to add 
new employee. If not warning message will pop up. Also, employee level
can be specified. When new employee is created we get notification
about the ID of the recently created employee.
