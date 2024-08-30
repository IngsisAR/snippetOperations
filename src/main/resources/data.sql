INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5704-48b1-98ad-e461a97f6fe8', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        'true', 'printlnNoExpressionArguments', 'LINTING', 'BOOLEAN' )
ON CONFLICT (id) DO NOTHING;

INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5701-48b1-98ad-e461a97f6fe8', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        'camel case', 'identifierCasing', 'LINTING', 'STRING' )
ON CONFLICT (id) DO NOTHING;

INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5701-48b1-98bd-e461a97f6fe8', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        'true', 'readInputNoExpressionArguments', 'LINTING', 'BOOLEAN' )
ON CONFLICT (id) DO NOTHING;

INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5701-48b1-98bd-f461a97f6fe8', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        '1', 'spaceBeforeColon', 'FORMATTING', 'INTEGER' )
ON CONFLICT (id) DO NOTHING;

INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5701-48b1-98bd-f462a97f6fe8', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        '1', 'spaceAfterColon', 'FORMATTING', 'INTEGER' )
ON CONFLICT (id) DO NOTHING;

INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5701-48b1-98bd-f461a90f6fe8', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        '1', 'spacesInAssignSymbol', 'FORMATTING', 'INTEGER' )
ON CONFLICT (id) DO NOTHING;

INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5701-48b1-98bd-f461a90f6fe6', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        '1', 'lineJumpBeforePrintln', 'FORMATTING', 'INTEGER' )
ON CONFLICT (id) DO NOTHING;

INSERT INTO rule (id, created_at, updated_at, default_value, name, rule_type, value_type)
VALUES ('59f5dd56-5701-48b1-98bd-f461a12f6fe6', '2024-07-10 01:19:47.404441', '2024-07-10 01:19:47.404441',
        '1', 'identationInsideConditionals', 'FORMATTING', 'INTEGER' )
ON CONFLICT (id) DO NOTHING;
