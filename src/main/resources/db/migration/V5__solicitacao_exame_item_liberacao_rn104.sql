-- docs/java-database-alignment.md §2.7 — RN-104: o resultado do exame so chega ao Responsavel
-- apos liberacao explicita do vet, distinta de RESULTADO_ENVIADO (que so indica que o vet recebeu/anexou).

ALTER TABLE TB_SOLICITACAO_EXAME_ITEM ADD (
    FL_LIBERADO_RESPONSAVEL    CHAR(1) DEFAULT 'N' NOT NULL,
    DT_LIBERACAO_RESPONSAVEL   DATE
);

ALTER TABLE TB_SOLICITACAO_EXAME_ITEM ADD CONSTRAINT CK_FL_LIBERADO CHECK (FL_LIBERADO_RESPONSAVEL IN ('S','N'));
