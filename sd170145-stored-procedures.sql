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
