CREATE TABLE Permission
(
	PERM_ID bigint NOT NULL, -- bigint je za MSSQL isto sto i long u javi/c#...
	PERM_NAME varchar(30) NOT NULL,
	PRIMARY KEY
	(
		PERM_ID ASC
	)
);

CREATE TABLE Role
(
	ROLE_ID bigint NOT NULL,
	ROLE_NAME varchar(30) NOT NULL,
	PRIMARY KEY
	(
		ROLE_ID ASC
	)
);

CREATE TABLE Role_permissions
(
	ROLE_ID bigint NOT NULL,
	PERM_ID bigint NOT NULL,
	PRIMARY KEY
	(
		ROLE_ID ASC,
		PERM_ID ASC
	)
);

ALTER TABLE Role_permissions
	WITH CHECK ADD CONSTRAINT FK_PERM_FOR_ROLE
	FOREIGN KEY (ROLE_ID)
	REFERENCES Role (ROLE_ID);

ALTER TABLE Role_permissions
	WITH CHECK ADD CONSTRAINT FK_ROLE_HAS_PERM
	FOREIGN KEY (PERM_ID)
	REFERENCES Permission (PERM_ID);

CREATE TABLE Users -- USER (case insensitive) je rezervisana rec http://stackoverflow.com/questions/695578/creating-table-names-that-are-reserved-words-keywords-in-ms-sql-server
(
	USER_ID bigint NOT NULL,
	USER_NAME varchar(30) UNIQUE NOT NULL,
	USER_PASS varchar(128) NOT NULL,
	USER_PASS_SALT varchar(128) NOT NULL,
	USER_ROLE_ID bigint NULL,
	PRIMARY KEY
	(
		USER_ID ASC
	)
);

ALTER TABLE Users
	WITH CHECK ADD CONSTRAINT FK_USER_ROLE
	FOREIGN KEY (USER_ROLE_ID)
	REFERENCES Role (ROLE_ID);

INSERT INTO Role VALUES (1, N'Predsednik skupstine');
INSERT INTO Role VALUES (2, N'Odbornik');
INSERT INTO Role VALUES (3, N'Građanin');