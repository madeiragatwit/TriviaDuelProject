CREATE DATABASE trivia_duel_project;

USE trivia_duel_project;

CREATE TABLE questions_ez(
	ID int auto_increment,
	QUESTION varchar(255) not null,
	ANSWER varchar(255) not null,
	created_at timestamp default current_timestamp,
	primary key(ID)
);

CREATE TABLE questions_med(
	ID int auto_increment,
	QUESTION varchar(255) not null,
	ANSWER varchar(255) not null,
	created_at timestamp default current_timestamp,
	primary key(ID)
);

CREATE TABLE questions_hard(
	ID int auto_increment,
	QUESTION varchar(255) not null,
	ANSWER varchar(255) not null,
	created_at timestamp default current_timestamp,
	primary key(ID)
);
