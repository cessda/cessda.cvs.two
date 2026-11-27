/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
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
import { readdirSync, readFileSync } from 'fs';
import { join } from 'path';

// AlertErrorComponent shows a field error from the server as 'error.<constraint>' with the field
// named by 'cvsApp.<entity>.<field>'. A missing key shows up as translation-not-found, so every
// constraint the DTOs declare needs both.
describe('Server validation messages', () => {
  const root = process.cwd();
  const en = JSON.parse(readFileSync(join(root, 'src/main/webapp/i18n/en.json'), 'utf-8'));
  const translate = (key: string): unknown => key.split('.').reduce((o, k) => o?.[k], en);
  const dtoDir = join(root, 'src/main/java/eu/cessda/cvs/service/dto');
  const constraint = /@(NotNull|NotBlank|NotEmpty|Size|Min|Max|DecimalMin|DecimalMax|Pattern|Email|AssertTrue)\b/;

  // the annotated members of a DTO, each with its constraints: a field or an isSomething() check
  const constrainedMembers = (source: string): Map<string, Set<string>> => {
    const members = new Map<string, Set<string>>();
    let pending: string[] = [];
    for (const line of source.split('\n')) {
      const annotation = constraint.exec(line);
      if (annotation) {
        pending.push(annotation[1]);
        continue;
      }
      const member = /^\s*(?:private|public)\s+[\w<>, ]+\s+(\w+)\s*(?:;|=|\()/.exec(line);
      if (member && pending.length) {
        const name = member[1].replace(/^is([A-Z])/, (_, c: string) => c.toLowerCase());
        members.set(name, new Set(pending));
      }
      if (!/^\s*(@|\/\/|$)/.test(line)) {
        pending = [];
      }
    }
    return members;
  };

  const dtos = readdirSync(dtoDir)
    .filter(f => f.endsWith('DTO.java'))
    .map(f => ({ entity: f.replace(/DTO\.java$/, ''), members: constrainedMembers(readFileSync(join(dtoDir, f), 'utf-8')) }))
    .filter(dto => dto.members.size > 0);

  it('should find the constraints of the agency', () => {
    const agency = dtos.find(dto => dto.entity === 'Agency');

    expect(agency?.members.get('uriCode')).toEqual(new Set(['NotBlank', 'Size']));
    expect(agency?.members.get('uriCodeWithPlaceholders')).toEqual(new Set(['AssertTrue']));
  });

  it('should have a message for every constraint', () => {
    // Min, Max and their decimal forms are shown as Size
    const codes = new Set(dtos.flatMap(dto => [...dto.members.values()].flatMap(c => [...c])));
    const missing = [...codes]
      .map(c => (/^(Decimal)?(Min|Max)$/.test(c) ? 'Size' : c))
      .filter(c => typeof translate(`error.${c}`) !== 'string');

    expect(missing).toEqual([]);
  });

  it('should name every agency field the server validates', () => {
    const agency = dtos.find(dto => dto.entity === 'Agency')!;
    const missing = [...agency.members.keys()].filter(field => typeof translate(`cvsApp.agency.${field}`) !== 'string');

    expect(missing).toEqual([]);
  });
});

// A BadRequestAlertException or ForbiddenAlertException reaches AlertErrorComponent as 'error.<errorKey>',
// in the error header and in the message, so every error key the server throws needs a message.
describe('Server alert messages', () => {
  const root = process.cwd();
  const en = JSON.parse(readFileSync(join(root, 'src/main/webapp/i18n/en.json'), 'utf-8'));
  const javaDir = join(root, 'src/main/java/eu/cessda/cvs');
  const javaFiles = (dir: string): string[] =>
    readdirSync(dir, { withFileTypes: true }).flatMap(e => (e.isDirectory() ? javaFiles(join(dir, e.name)) : e.name.endsWith('.java') ? [join(dir, e.name)] : []));

  // the last string argument of 'new BadRequestAlertException(..., "key")' or of the super(...) call in a subclass
  const errorKeys = javaFiles(javaDir).flatMap(file => {
    const source = readFileSync(file, 'utf-8');
    const subclass = /extends (BadRequestAlertException|ForbiddenAlertException)\b/.test(source);
    const calls = [...source.matchAll(/new (?:BadRequestAlertException|ForbiddenAlertException)\(([^;]*)\);/g)].map(m => m[1]);
    if (subclass) {
      calls.push(...[...source.matchAll(/super\(([^;]*)\);/g)].map(m => m[1]));
    }
    return calls.map(args => /"([^"]+)"\s*$/.exec(args)?.[1]).filter((key): key is string => !!key);
  });

  it('should find the error keys of the code and authority exceptions', () => {
    expect(errorKeys).toEqual(expect.arrayContaining(['codeexists', 'insufficientvocabularyauthority', 'idexists']));
  });

  it('should have a message for every error key', () => {
    const missing = [...new Set(errorKeys)].filter(key => typeof en.error[key] !== 'string');

    expect(missing).toEqual([]);
  });
});
