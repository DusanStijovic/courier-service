USE [kurirskaSluzba]
GO

DELETE FROM [dbo].[ZahtevZaKurira]
DELETE FROM [dbo].[Administrator]
DELETE FROM [dbo].[Kupac]
DELETE FROM [dbo].[Kurir]
DELETE FROM [dbo].[Korisnik]
DELETE FROM [dbo].[Vozilo]
DELETE FROM [dbo].Paket
DELETE FROM [dbo].[zahtevZaPrevozom]
DELETE FROM [dbo].[Magacin]
DELETE FROM [dbo].[Adresa]
DELETE FROM [dbo].[Grad]


SET IDENTITY_INSERT [dbo].[Grad] ON 
GO

INSERT INTO [dbo].[Grad] ([idGrad], [naziv], [postanskiBroj])
VALUES (1, 'Beograd', 1100)
GO

INSERT INTO [dbo].[Grad] ([idGrad], [naziv], [postanskiBroj])
VALUES (2, 'Arandjelovac', 34300)
GO

SET IDENTITY_INSERT [dbo].[Grad] OFF
GO

SET IDENTITY_INSERT [dbo].[Adresa] ON
GO

INSERT INTO [dbo].[Adresa] ([idAdresa], [ulica], [broj], [xKoordinata], [yKoordinata], [idGrad])
VALUES (1,'Bulevar Kralja Aleksandra', 46, 10,10,1)
GO

INSERT INTO [dbo].[Adresa] ([idAdresa], [ulica], [broj], [xKoordinata], [yKoordinata], [idGrad])
VALUES (2,'Kralja Petra Prvog', 46, 10,10,2)
GO

SET IDENTITY_INSERT [dbo].[Adresa] OFF
GO

INSERT INTO [dbo].[Korisnik]([ime],[prezime],[korisnickoIme],[sifra],[idAdresa])
VALUES ('Dusan', 'Stijovic', 'ducati', 'ducati',1)
GO

INSERT INTO [dbo].[Korisnik]([ime],[prezime],[korisnickoIme],[sifra],[idAdresa])
VALUES ('Tamara', 'Sekularac', 'tasha', 'tasha',2)
GO

INSERT INTO [dbo].[Korisnik]([ime],[prezime],[korisnickoIme],[sifra],[idAdresa])
VALUES ('Milos', 'Cvetanovic', 'cmiki', 'cmiki',2)
GO

INSERT INTO [dbo].[Kurir] ([brojIsporucenihPaketa] ,[profit], [status], [korisnickoIme], [brojVozackeDozvole])
VALUES (0, 0, 0, 'cmiki', '1111111111')
GO

INSERT INTO [dbo].[Administrator] ([korisnickoIme])
VALUES ('tasha')
GO

INSERT INTO [dbo].[Kupac] ([korisnickoIme])
VALUES ('ducati')
GO

INSERT INTO [dbo].[Magacin] ([idMagacin])
VALUES (1)
GO

INSERT INTO [dbo].[Vozilo] ([registracioniBroj],[tipGoriva],[potrosnja],[nosivost])
VALUES ('999999999',2,4,1000)
GO

INSERT INTO [dbo].[ZahtevZaKurira] ([brojVozackeDozvole], [korisnickoIme])
VALUES ('999999999','ducati') 
GO

INSERT INTO [dbo].[MagacinVozilo]([registracioniBroj],[idMagacin])
VALUES ('999999999', 1)
GO










