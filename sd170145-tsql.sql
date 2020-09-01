USE kurirskaSluzba
GO
-- =============================================
-- Author:		Dusan Stijovic
-- =============================================

DROP FUNCTION IF EXISTS  [dbo].izracunajCenuPaketa

GO

CREATE FUNCTION [dbo].izracunajCenuPaketa(
        @tipPaketa int,
        @tezinaPaketa decimal(10,3),
        @adresaPreuzimanja int,
        @adresaIsporuke int
    )
    RETURNS DECIMAL(10,3)
    AS
    BEGIN
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
GO

-- =============================================
-- Author:		Dušan Stijović
-- Create date: 04.06.2020
-- Description:	Kreiranje ponude za korisnika
-- =============================================
USE kurirskaSluzba
GO

DROP TRIGGER IF EXISTS [dbo].kreirajPonudu
GO

CREATE TRIGGER [dbo].kreirajPonudu 
    ON [dbo].zahtevZaPrevozom
    AFTER INSERT
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
    GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================

USE kurirskaSluzba
GO

DROP TRIGGER IF EXISTS [dbo].kreirajIstorijuVozila
GO


CREATE TRIGGER [dbo].kreirajIstorijuVozila
    ON  [dbo].VozilaUVoznji
    AFTER INSERT
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
    GO


