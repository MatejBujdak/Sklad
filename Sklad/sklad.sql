-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: localhost:3307
-- Čas generovania: Št 30.Nov 2023, 15:07
-- Verzia serveru: 10.4.27-MariaDB
-- Verzia PHP: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `sklad`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `distribucie`
--

CREATE TABLE `distribucie` (
  `id` int(11) NOT NULL,
  `tovar_id` int(11) NOT NULL,
  `dodavatel_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `distribucie`
--

INSERT INTO `distribucie` (`id`, `tovar_id`, `dodavatel_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 3),
(5, 4, 2),
(6, 5, 3),
(8, 3, 2),
(9, 7, 2);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `dodavatelia`
--

CREATE TABLE `dodavatelia` (
  `id` int(11) NOT NULL,
  `nazov` varchar(50) NOT NULL,
  `ico` varchar(40) NOT NULL,
  `adresa` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `dodavatelia`
--

INSERT INTO `dodavatelia` (`id`, `nazov`, `ico`, `adresa`) VALUES
(1, 'Electro', '12345678', 'adresa123456'),
(2, 'ElectroShop', '98765432', 'adresa12345'),
(3, 'TechShop', '11223344', 'adresa1234');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `tovar`
--

CREATE TABLE `tovar` (
  `id` int(11) NOT NULL,
  `nazov` varchar(50) NOT NULL,
  `cena` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `tovar`
--

INSERT INTO `tovar` (`id`, `nazov`, `cena`) VALUES
(1, 'Televízia', 599.99),
(2, 'Notebook', 899.99),
(3, 'Chladnička', 499.99),
(4, 'Mikrovlnná rúra', 129.99),
(5, 'Kávovar', 79.99),
(6, 'Monitor', 159.99),
(7, 'Klavesnica', 49.52);

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `distribucie`
--
ALTER TABLE `distribucie`
  ADD PRIMARY KEY (`id`);

--
-- Indexy pre tabuľku `dodavatelia`
--
ALTER TABLE `dodavatelia`
  ADD PRIMARY KEY (`id`);

--
-- Indexy pre tabuľku `tovar`
--
ALTER TABLE `tovar`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `distribucie`
--
ALTER TABLE `distribucie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pre tabuľku `dodavatelia`
--
ALTER TABLE `dodavatelia`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pre tabuľku `tovar`
--
ALTER TABLE `tovar`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
