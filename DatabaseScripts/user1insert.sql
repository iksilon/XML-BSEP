USE [XML-BSEP]
GO

INSERT INTO [dbo].[Users]
           ([LAST_NAME]
           ,[name]
           ,[password]
           ,[PASSWORD_SALT]
           ,[username]
           ,[role_id])
     VALUES
           (NULL
           ,NULL
           ,'bxi+3EEzP0wgWh6G93+by8sFVyU='
           ,'1r7D9Liamag='
           ,'user1@email.com'
           ,2) --stavi broj koji ti je za ROLE odbornik
GO
