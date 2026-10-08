ALTER TABLE favoritos 
ADD COLUMN lista_id INT,
ADD CONSTRAINT fk_favorito_lista FOREIGN KEY (lista_id) REFERENCES listas(id);