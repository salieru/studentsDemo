drop table if exists students;
drop table if exists groups;

create table STUDENTS(
  STUDENT_ID numeric identity primary key,
  FIRST_NAME varchar(100) not null,
  LAST_NAME varchar(100) not null,
  MIDDLE_NAME varchar(100) not null,
  BIRTH_DATE date not null,
  GROUP_ID numeric not null
);

CREATE TABLE GROUPZ(
  group_id numeric identity primary key,
  group_number int NOT NULL,
  faculty_name varchar(100) not null
);

ALTER TABLE STUDENTS ADD FOREIGN KEY (group_id) REFERENCES GROUPZ(group_id);