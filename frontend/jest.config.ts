export default {
    preset: 'jest-preset-angular',
    setupFilesAfterEnv: ['<rootDir>/src/test-setup.ts'],
    coverageReporters: ['clover', 'json', 'lcov', 'text', 'text-summary'],
    collectCoverage: true,
    collectCoverageFrom: [
        "**/*.{ts,tsx}",
        "!**/coverage/**",
        "!**/vendor/**"
    ],
    coverageThreshold: {
        global: {
            branches: 85,
            functions: 90,
            lines: 90,
            statements: -10
        },
    },
    coveragePathIgnorePatterns: [
        "main.server.ts",
        "app.module.server.ts",
        ".model.ts",
        ".enum.ts",
        ".constants.ts",
        ".config.ts",
        "environment.",
        "main.ts",
        ".guard.ts",
        "app.module.ts",
        "index.ts",
        "app-routing.module.ts",
        "app.routes.ts",
        "router.mock.ts"
    ],
    transform: {
        '^.+\\.(ts|mjs|js|html)$': [
            'jest-preset-angular',
            {
                tsconfig: '<rootDir>/tsconfig.spec.json',
                stringifyContentPathRegex: '\\.(html|svg)$',
            },
        ],
    },
    transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$||@arcgis|@esri|.pnpm|ol/|ol/source|ol|quick-lru|color-(space|parse|rgba|name)/)'],
    snapshotSerializers: [
        'jest-preset-angular/build/serializers/no-ng-attributes',
        'jest-preset-angular/build/serializers/ng-snapshot',
        'jest-preset-angular/build/serializers/html-comment',
    ],
}