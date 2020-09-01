EXEC msdb.dbo.sp_delete_database_backuphistory @database_name = N'kurirskaSluzba'
GO
use [kurirskaSluzba];
GO
use [master];
GO
USE [master]
GO
ALTER DATABASE [kurirskaSluzba] SET  SINGLE_USER WITH ROLLBACK IMMEDIATE
GO
USE [master]
GO
/****** Object:  Database [kurirskaSluzba]    Script Date: 6/24/2020 9:41:10 AM ******/
DROP DATABASE IF EXISTS [kurirskaSluzba]
GO
CREATE DATABASE [kurirskaSluzba]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'kurirskaSluzba', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL15.MSSQLSERVER\MSSQL\DATA\kurirskaSluzba.mdf' , SIZE = 8192KB , FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'kurirskaSluzba_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL15.MSSQLSERVER\MSSQL\DATA\kurirskaSluzba_log.ldf' , SIZE = 8192KB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [kurirskaSluzba] SET COMPATIBILITY_LEVEL = 150
GO
ALTER DATABASE [kurirskaSluzba] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET ARITHABORT OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET AUTO_CREATE_STATISTICS ON(INCREMENTAL = OFF)
GO
ALTER DATABASE [kurirskaSluzba] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [kurirskaSluzba] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [kurirskaSluzba] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET  DISABLE_BROKER 
GO
ALTER DATABASE [kurirskaSluzba] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [kurirskaSluzba] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [kurirskaSluzba] SET  READ_WRITE 
GO
ALTER DATABASE [kurirskaSluzba] SET RECOVERY FULL 
GO
ALTER DATABASE [kurirskaSluzba] SET  MULTI_USER 
GO
ALTER DATABASE [kurirskaSluzba] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [kurirskaSluzba] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [kurirskaSluzba] SET DELAYED_DURABILITY = DISABLED 
GO
USE [kurirskaSluzba]
GO
ALTER DATABASE SCOPED CONFIGURATION SET LEGACY_CARDINALITY_ESTIMATION = Off;
GO
ALTER DATABASE SCOPED CONFIGURATION FOR SECONDARY SET LEGACY_CARDINALITY_ESTIMATION = Primary;
GO
ALTER DATABASE SCOPED CONFIGURATION SET MAXDOP = 0;
GO
ALTER DATABASE SCOPED CONFIGURATION FOR SECONDARY SET MAXDOP = PRIMARY;
GO
ALTER DATABASE SCOPED CONFIGURATION SET PARAMETER_SNIFFING = On;
GO
ALTER DATABASE SCOPED CONFIGURATION FOR SECONDARY SET PARAMETER_SNIFFING = Primary;
GO
ALTER DATABASE SCOPED CONFIGURATION SET QUERY_OPTIMIZER_HOTFIXES = Off;
GO
ALTER DATABASE SCOPED CONFIGURATION FOR SECONDARY SET QUERY_OPTIMIZER_HOTFIXES = Primary;
GO
USE [kurirskaSluzba]
GO
IF NOT EXISTS (SELECT name FROM sys.filegroups WHERE is_default=1 AND name = N'PRIMARY') ALTER DATABASE [kurirskaSluzba] MODIFY FILEGROUP [PRIMARY] DEFAULT
GO




CREATE TABLE [Administrator]
( 
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [Administrator]
	ADD CONSTRAINT [XPKAdministrator] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

CREATE TABLE [Adresa]
( 
	[idAdresa]           int  IDENTITY ( 1,1 )  NOT NULL ,
	[ulica]              varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[broj]               int  NOT NULL ,
	[xKoordinata]        int  NOT NULL ,
	[yKoordinata]        int  NOT NULL ,
	[idGrad]             int  NOT NULL 
)
go

ALTER TABLE [Adresa]
	ADD CONSTRAINT [XPKAdresa] PRIMARY KEY  CLUSTERED ([idAdresa] ASC)
go

CREATE TABLE [Grad]
( 
	[idGrad]             int  IDENTITY ( 1,1 )  NOT NULL ,
	[naziv]              varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[postanskiBroj]      varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([idGrad] ASC)
go

CREATE TABLE [Korisnik]
( 
	[ime]                varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[prezime]            varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[sifra]              varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[idAdresa]           int  NOT NULL 
)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XPKKorisnik] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

CREATE TABLE [Kupac]
( 
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [Kupac]
	ADD CONSTRAINT [XPKKupac] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

CREATE TABLE [Kurir]
( 
	[brojIsporucenihPaketa] int  NOT NULL 
	CONSTRAINT [Zero_52415437]
		 DEFAULT  0,
	[profit]             decimal(10,3)  NOT NULL 
	CONSTRAINT [Zero_864666056]
		 DEFAULT  0,
	[status]             int  NULL 
	CONSTRAINT [Zero_630706377]
		 DEFAULT  0,
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[brojVozackeDozvole] varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [Kurir]
	 WITH CHECK ADD CONSTRAINT [StatusKurira_109280677] CHECK  ( status BETWEEN 0 AND 1 )
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XPKKurir] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [brojVozackeDozvole] UNIQUE ([brojVozackeDozvole]  ASC)
go

CREATE TABLE [Kurir_Vozilo]
( 
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[registracioniBroj]  varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [Kurir_Vozilo]
	ADD CONSTRAINT [XPKKurir_Vozilo] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC,[registracioniBroj] ASC)
go

CREATE TABLE [Magacin]
( 
	[idMagacin]          int  NOT NULL 
)
go

ALTER TABLE [Magacin]
	ADD CONSTRAINT [XPKMagacin] PRIMARY KEY  CLUSTERED ([idMagacin] ASC)
go

CREATE TABLE [MagacinPaketi]
( 
	[idPaketa]           int  NOT NULL ,
	[idMagacin]          int  NOT NULL 
)
go

ALTER TABLE [MagacinPaketi]
	ADD CONSTRAINT [XPKMagacinPaketi] PRIMARY KEY  CLUSTERED ([idPaketa] ASC)
go

CREATE TABLE [MagacinVozilo]
( 
	[registracioniBroj]  varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[idMagacin]          int  NOT NULL 
)
go

ALTER TABLE [MagacinVozilo]
	ADD CONSTRAINT [XPKMagacinVozilo] PRIMARY KEY  CLUSTERED ([registracioniBroj] ASC)
go

CREATE TABLE [Paket]
( 
	[statusPaketa]       int  NOT NULL ,
	[cena]               decimal(10,3)  NOT NULL ,
	[vremePrihvatanjaZahteva] datetime  NULL ,
	[vremeKreiranjaZahteva] datetime  NOT NULL ,
	[idPaketa]           int  NOT NULL ,
	[trenutnaLokacija]   int  NULL ,
	[oznacenZaPreuzimanje] int  NULL 
	CONSTRAINT [Zero_957744345]
		 DEFAULT  0
)
go

ALTER TABLE [Paket]
	 WITH CHECK ADD CONSTRAINT [statusPaketa_168145459] CHECK  ( statusPaketa BETWEEN 0 AND 4 )
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [XPKPaket] PRIMARY KEY  CLUSTERED ([idPaketa] ASC)
go

CREATE TABLE [PaketVozilo]
( 
	[idPaketa]           int  NOT NULL ,
	[registracioniBroj]  varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL 
)
go

ALTER TABLE [PaketVozilo]
	ADD CONSTRAINT [XPKPaketVozilo] PRIMARY KEY  CLUSTERED ([idPaketa] ASC)
go

CREATE TABLE [VozilaUVoznji]
( 
	[registracioniBroj]  varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [VozilaUVoznji]
	ADD CONSTRAINT [XPKVozilaUVoznji] PRIMARY KEY  CLUSTERED ([registracioniBroj] ASC)
go

CREATE TABLE [Vozilo]
( 
	[registracioniBroj]  varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[tipGoriva]          int  NOT NULL ,
	[potrosnja]          decimal(10,3)  NOT NULL ,
	[nosivost]           decimal(10,3)  NOT NULL 
)
go

ALTER TABLE [Vozilo]
	 WITH CHECK ADD CONSTRAINT [tipGoriva_2021031306] CHECK  ( tipGoriva BETWEEN 0 AND 2 )
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XPKVozilo] PRIMARY KEY  CLUSTERED ([registracioniBroj] ASC)
go

CREATE TABLE [ZahtevZaKurira]
( 
	[brojVozackeDozvole] varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL ,
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [ZahtevZaKurira]
	ADD CONSTRAINT [XPKZahtevZaKurira] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

CREATE TABLE [zahtevZaPrevozom]
( 
	[idZahteva]          int  IDENTITY ( 1,1 )  NOT NULL ,
	[tipPaketa]          int  NOT NULL ,
	[tezinaPaketa]       decimal(10,3)  NOT NULL ,
	[adresaPreuzimanja]  int  NOT NULL ,
	[adresaIsporuke]     int  NOT NULL ,
	[korisnickoIme]      varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL 
)
go

ALTER TABLE [zahtevZaPrevozom]
	 WITH CHECK ADD CONSTRAINT [tipPaketa_1112841168] CHECK  ( tipPaketa BETWEEN 0 AND 4 )
go

ALTER TABLE [zahtevZaPrevozom]
	ADD CONSTRAINT [XPKzahtevZaPrevozom] PRIMARY KEY  CLUSTERED ([idZahteva] ASC)
go


ALTER TABLE [Administrator] WITH CHECK 
	ADD CONSTRAINT [R_10] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Administrator]
	  WITH CHECK CHECK CONSTRAINT [R_10]
go


ALTER TABLE [Adresa] WITH CHECK 
	ADD CONSTRAINT [R_4] FOREIGN KEY ([idGrad]) REFERENCES [Grad]([idGrad])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Adresa]
	  WITH CHECK CHECK CONSTRAINT [R_4]
go


ALTER TABLE [Korisnik] WITH CHECK 
	ADD CONSTRAINT [R_7] FOREIGN KEY ([idAdresa]) REFERENCES [Adresa]([idAdresa])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Korisnik]
	  WITH CHECK CHECK CONSTRAINT [R_7]
go


ALTER TABLE [Kupac] WITH CHECK 
	ADD CONSTRAINT [R_8] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Kupac]
	  WITH CHECK CHECK CONSTRAINT [R_8]
go


ALTER TABLE [Kurir] WITH CHECK 
	ADD CONSTRAINT [R_9] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Kurir]
	  WITH CHECK CHECK CONSTRAINT [R_9]
go


ALTER TABLE [Kurir_Vozilo] WITH CHECK 
	ADD CONSTRAINT [R_15] FOREIGN KEY ([korisnickoIme]) REFERENCES [Kurir]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Kurir_Vozilo]
	  WITH CHECK CHECK CONSTRAINT [R_15]
go

ALTER TABLE [Kurir_Vozilo] WITH CHECK 
	ADD CONSTRAINT [R_16] FOREIGN KEY ([registracioniBroj]) REFERENCES [Vozilo]([registracioniBroj])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Kurir_Vozilo]
	  WITH CHECK CHECK CONSTRAINT [R_16]
go


ALTER TABLE [Magacin] WITH CHECK 
	ADD CONSTRAINT [R_27] FOREIGN KEY ([idMagacin]) REFERENCES [Adresa]([idAdresa])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Magacin]
	  WITH CHECK CHECK CONSTRAINT [R_27]
go


ALTER TABLE [MagacinPaketi] WITH CHECK 
	ADD CONSTRAINT [R_31] FOREIGN KEY ([idPaketa]) REFERENCES [Paket]([idPaketa])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [MagacinPaketi]
	  WITH CHECK CHECK CONSTRAINT [R_31]
go

ALTER TABLE [MagacinPaketi] WITH CHECK 
	ADD CONSTRAINT [R_32] FOREIGN KEY ([idMagacin]) REFERENCES [Magacin]([idMagacin])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [MagacinPaketi]
	  WITH CHECK CHECK CONSTRAINT [R_32]
go


ALTER TABLE [MagacinVozilo] WITH CHECK 
	ADD CONSTRAINT [R_33] FOREIGN KEY ([registracioniBroj]) REFERENCES [Vozilo]([registracioniBroj])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [MagacinVozilo]
	  WITH CHECK CHECK CONSTRAINT [R_33]
go

ALTER TABLE [MagacinVozilo] WITH CHECK 
	ADD CONSTRAINT [R_34] FOREIGN KEY ([idMagacin]) REFERENCES [Magacin]([idMagacin])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [MagacinVozilo]
	  WITH CHECK CHECK CONSTRAINT [R_34]
go


ALTER TABLE [Paket] WITH CHECK 
	ADD CONSTRAINT [R_21] FOREIGN KEY ([idPaketa]) REFERENCES [zahtevZaPrevozom]([idZahteva])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	  WITH CHECK CHECK CONSTRAINT [R_21]
go

ALTER TABLE [Paket] WITH CHECK 
	ADD CONSTRAINT [R_20] FOREIGN KEY ([trenutnaLokacija]) REFERENCES [Adresa]([idAdresa])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	  WITH CHECK CHECK CONSTRAINT [R_20]
go


ALTER TABLE [PaketVozilo] WITH CHECK 
	ADD CONSTRAINT [R_43] FOREIGN KEY ([idPaketa]) REFERENCES [Paket]([idPaketa])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [PaketVozilo]
	  WITH CHECK CHECK CONSTRAINT [R_43]
go

ALTER TABLE [PaketVozilo] WITH CHECK 
	ADD CONSTRAINT [R_44] FOREIGN KEY ([registracioniBroj]) REFERENCES [Vozilo]([registracioniBroj])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [PaketVozilo]
	  WITH CHECK CHECK CONSTRAINT [R_44]
go


ALTER TABLE [VozilaUVoznji] WITH CHECK 
	ADD CONSTRAINT [R_41] FOREIGN KEY ([registracioniBroj]) REFERENCES [Vozilo]([registracioniBroj])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [VozilaUVoznji]
	  WITH CHECK CHECK CONSTRAINT [R_41]
go

ALTER TABLE [VozilaUVoznji] WITH CHECK 
	ADD CONSTRAINT [R_42] FOREIGN KEY ([korisnickoIme]) REFERENCES [Kurir]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [VozilaUVoznji]
	  WITH CHECK CHECK CONSTRAINT [R_42]
go


ALTER TABLE [ZahtevZaKurira] WITH CHECK 
	ADD CONSTRAINT [R_56] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [ZahtevZaKurira]
	  WITH CHECK CHECK CONSTRAINT [R_56]
go


ALTER TABLE [zahtevZaPrevozom] WITH CHECK 
	ADD CONSTRAINT [R_19] FOREIGN KEY ([adresaPreuzimanja]) REFERENCES [Adresa]([idAdresa])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [zahtevZaPrevozom]
	  WITH CHECK CHECK CONSTRAINT [R_19]
go

ALTER TABLE [zahtevZaPrevozom] WITH CHECK 
	ADD CONSTRAINT [R_35] FOREIGN KEY ([adresaIsporuke]) REFERENCES [Adresa]([idAdresa])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [zahtevZaPrevozom]
	  WITH CHECK CHECK CONSTRAINT [R_35]
go

ALTER TABLE [zahtevZaPrevozom] WITH CHECK 
	ADD CONSTRAINT [R_39] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [zahtevZaPrevozom]
	  WITH CHECK CHECK CONSTRAINT [R_39]
go

CREATE OR ALTER FUNCTION [izracunajCenuPaketa] (@tipPaketa int , @tezinaPaketa decimal(10,3) , @adresaPreuzimanja int , @adresaIsporuke int )  
  RETURNS decimal(10,3) 
  
AS BEGIN
	DECLARE @osnovnaCena int
	DECLARE @cenaPoKilogramu int

	IF (@tipPaketa = 0) 
		BEGIN
			SET @osnovnaCena = 115
			SET @cenaPoKilogramu =0
		END
	IF (@tipPaketa = 1)
		BEGIN
			SET @osnovnaCena = 175
			SET @cenaPoKilogramu = 100
		END
	IF (@tipPaketa = 2)
		BEGIN
			SET @osnovnaCena = 250
			SET @cenaPoKilogramu = 100
		END
	IF (@tipPaketa = 3) 
		BEGIN
			SET @osnovnaCena = 350
			SET @cenaPoKilogramu = 500
		END

		DECLARE @startX int, @startY int
		DECLARE @endX int , @endY int

		SELECT @startX = xKoordinata, @startY = yKoordinata
		FROM Adresa
		WHERE idAdresa = @adresaPreuzimanja

		SELECT @endX = xKoordinata, @endY = yKoordinata
		FROM Adresa
		WHERE idAdresa = @adresaIsporuke

		DECLARE @euclidDistance decimal(10,3)

		SET @euclidDistance = SQRT(SQUARE(@startX - @endX) + SQUARE(@startY - @endY))

		DECLARE @cenaPaketa decimal(10,3)

		SET @cenaPaketa = (@osnovnaCena + @tezinaPaketa*@cenaPoKilogramu)*@euclidDistance

	    RETURN @cenaPaketa
END
go

CREATE TRIGGER [kreirajIstorijuVozila] ON VozilaUVoznji
   WITH 
 EXECUTE AS CALLER  AFTER INSERT 
  
  AS

BEGIN
	SET NOCOUNT ON;

	DECLARE @vozila CURSOR

	DECLARE @korisnickoIme varchar(100), @registracioniBroj varchar(100)


	SET @vozila = CURSOR FOR
	SELECT korisnickoIme, registracioniBroj
	FROM INSERTED

	OPEN @vozila

	FETCH NEXT 
	FROM @vozila
	INTO @korisnickoIme, @registracioniBroj

	WHILE @@FETCH_STATUS = 0
		BEGIN

		DECLARE @postoji int
			SET @postoji = (
							SELECT COUNT(korisnickoIme)
							FROM Kurir_Vozilo
							WHERE korisnickoIme = @korisnickoIme AND registracioniBroj = @registracioniBroj
						   )


			IF (@postoji = 0)
				BEGIN
					INSERT INTO Kurir_Vozilo(korisnickoIme,registracioniBroj)
					VALUES(@korisnickoIme, @registracioniBroj)
				END
				


		
			FETCH NEXT
			FROM @vozila
			INTO @korisnickoIme, @registracioniBroj
		END
END

 
go


ENABLE TRIGGER [kreirajIstorijuVozila] ON VozilaUVoznji
go

CREATE TRIGGER [kreirajPonudu] ON zahtevZaPrevozom
   WITH 
 EXECUTE AS CALLER  AFTER INSERT 
  
  AS

BEGIN
	SET NOCOUNT ON;

	DECLARE @redovi CURSOR;

	DECLARE @idZahteva int
	DECLARE @tipPaketa int
	DECLARE @tezinaPaketa decimal(10,3)
	DECLARE @adresaPreuzimanja int
	DECLARE @adresaIsporuke int

	SET @redovi = CURSOR FOR
	SELECT idZahteva, tipPaketa, tezinaPaketa, adresaPreuzimanja, adresaIsporuke
	FROM INSERTED

	OPEN @redovi

	FETCH NEXT
	FROM @redovi 
	INTO @idZahteva, @tipPaketa, @tezinaPaketa, @adresaPreuzimanja, @adresaIsporuke
	
	WHILE @@FETCH_STATUS = 0
		BEGIN
		    DECLARE @cenaPaketa int;
			SET @cenaPaketa = [dbo].izracunajCenuPaketa(@tipPaketa, @tezinaPaketa, @adresaPreuzimanja, @adresaIsporuke)

			INSERT INTO [dbo].Paket (idPaketa, statusPaketa,cena,vremePrihvatanjaZahteva, vremeKreiranjaZahteva, trenutnaLokacija)
			VALUES(@idZahteva, 0, @cenaPaketa, NULL, CURRENT_TIMESTAMP, @adresaPreuzimanja);



			FETCH NEXT
			FROM @redovi 
			INTO @idZahteva, @tipPaketa, @tezinaPaketa, @adresaPreuzimanja, @adresaIsporuke
		END;

   

END

 
go


ENABLE TRIGGER [kreirajPonudu] ON zahtevZaPrevozom
go
