CREATE TABLE clients (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE portfolios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- For Enum PortfolioType
    current_weight DECIMAL(10, 2) NOT NULL,
    client_id BIGINT,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticker VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    current_price DECIMAL(10, 2) NOT NULL,
    ceiling_price DECIMAL(10, 2) NOT NULL,
    current_weight DECIMAL(10, 2) NOT NULL,
    quantity INT,
    portfolio_id BIGINT,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE SET NULL
);

CREATE TABLE stocks (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES assets(id) ON DELETE CASCADE
);

CREATE TABLE reits (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES assets(id) ON DELETE CASCADE
);

CREATE TABLE fixed_incomes (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES assets(id) ON DELETE CASCADE
);

CREATE TABLE international_assets (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES assets(id) ON DELETE CASCADE
);

INSERT INTO clients (name) VALUES ('Joao');

INSERT INTO portfolios (type, current_weight, client_id) VALUES ('STOCKS', 30.0, 1);
SET @portfolio_id = LAST_INSERT_ID();

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('AGRO3', 'Brasilagro - Companhia Brasileira de Propriedades Agrícolas', 50.00, 60.00, 20.00, 10, @portfolio_id);
INSERT INTO stocks (id) VALUES (LAST_INSERT_ID());

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('ALUP11', 'ALUPAR UNT', 30.00, 70.00, 20.00, 10, @portfolio_id);
INSERT INTO stocks (id) VALUES (LAST_INSERT_ID());

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('B3SA3', 'B3 S.A - Brasil, Bolsa, Balcão', 50.00, 60.00, 20.00, 10, @portfolio_id);
INSERT INTO stocks (id) VALUES (LAST_INSERT_ID());


INSERT INTO portfolios (type, current_weight, client_id) VALUES ('REITS', 30.0, 1);
SET @portfolio_id = LAST_INSERT_ID();

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('ALZR11', 'FDO INV IMOB ALIANZA TRUST RENDA IMOB CF', 104.78, 120.00, 10.00, 1, @portfolio_id);
INSERT INTO reits (id) VALUES (LAST_INSERT_ID());

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('BTLG11', 'BTG PACTUAL LOGISTICA FDO INV IMOB - FII ETF', 97.29, 105.00, 10.00, 1, @portfolio_id);
INSERT INTO reits (id) VALUES (LAST_INSERT_ID());

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('HGLG11', 'FDO INV IMOB CSHG LOGISTICA CF', 157.32, 170.00, 10.00, 1, @portfolio_id);
INSERT INTO reits (id) VALUES (LAST_INSERT_ID());


INSERT INTO portfolios (type, current_weight, client_id) VALUES ('INTERNATIONAL', 50.0, 1);
SET @portfolio_id = LAST_INSERT_ID();

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('VOO', 'Vanguard 500 Idx ETF', 535.50, 1000.00, 25.00, 1, @portfolio_id);
INSERT INTO international_assets (id) VALUES (LAST_INSERT_ID());

INSERT INTO assets (ticker, name, current_price, ceiling_price, current_weight, quantity, portfolio_id)
VALUES ('QQQ', 'Invesco QQQ Trust 1', 491.45, 1000.00, 25.00, 1, @portfolio_id);
INSERT INTO international_assets (id) VALUES (LAST_INSERT_ID());

INSERT INTO clients (name) VALUES ('Maria');
INSERT INTO clients (name) VALUES ('Marcos');


