/* Add new columns and update column with content */
ALTER TABLE users ADD country varchar(50), ADD age int not null;
 
UPDATE users SET age=0;