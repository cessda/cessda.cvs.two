/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const tsconfig = require('../../../tsconfig.json');

module.exports = {
    preset: 'jest-preset-angular',
    setupFiles: ['jest-date-mock'],
    setupFilesAfterEnv: ['<rootDir>/src/test/javascript/jest.ts'],
    cacheDirectory: '<rootDir>/target/jest-cache',
    coverageDirectory: '<rootDir>/target/test-results/',
    globals: {
        'ts-jest': {
            stringifyContentPathRegex: '\\.html$',
            tsconfig: '<rootDir>/tsconfig.json'
        }
    },
    coveragePathIgnorePatterns: [
        '/node_modules/',
        '<rootDir>/src/test/javascript'
    ],
    moduleNameMapper: mapTypescriptAliasToJestAlias(),
    reporters: [
        'default',
        [ 'jest-junit', { outputDirectory: './target/test-results/', outputName: 'TESTS-results-jest.xml' } ]
    ],
    testResultsProcessor: 'jest-sonar-reporter',
    transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$)'],
    testMatch: ['<rootDir>/src/test/javascript/spec/**/@(*.)@(spec.ts)'],
    testRunner: 'jasmine2',
    rootDir: '../../../',
    testURL: 'http://localhost/'
};

function mapTypescriptAliasToJestAlias(alias = {}) {
    const jestAliases = { ...alias };
    if (!tsconfig.compilerOptions.paths) {
        return jestAliases;
    }
    Object.entries(tsconfig.compilerOptions.paths)
        .filter(([key, value]) => {
            // use Typescript alias in Jest only if this has value
            if (value.length) {
                return true;
            }
            return false;
        })
        .map(([key, value]) => {
            // if Typescript alias ends with /* then in Jest:
            // - alias key must end with /(.*)
            // - alias value must end with /$1
            const regexToReplace = /(.*)\/\*$/;
            const aliasKey = key.replace(regexToReplace, '$1/(.*)');
            const aliasValue = value[0].replace(regexToReplace, '$1/$$1');
            return [aliasKey, `<rootDir>/${aliasValue}`];
        })
        .reduce((aliases, [key, value]) => {
            aliases[key] = value;
            return aliases;
        }, jestAliases);
    return jestAliases;
}
