/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { VocabularyComponentsPage, VocabularyDeleteDialog, VocabularyUpdatePage } from './vocabulary.page-object';

const expect = chai.expect;

describe('Vocabulary e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let vocabularyComponentsPage: VocabularyComponentsPage;
  let vocabularyUpdatePage: VocabularyUpdatePage;
  let vocabularyDeleteDialog: VocabularyDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Vocabularies', async () => {
    await navBarPage.goToEntity('vocabulary');
    vocabularyComponentsPage = new VocabularyComponentsPage();
    await browser.wait(ec.visibilityOf(vocabularyComponentsPage.title), 5000);
    expect(await vocabularyComponentsPage.getTitle()).to.eq('cvsApp.vocabulary.home.title');
    await browser.wait(ec.or(ec.visibilityOf(vocabularyComponentsPage.entities), ec.visibilityOf(vocabularyComponentsPage.noResult)), 1000);
  });

  it('should load create Vocabulary page', async () => {
    await vocabularyComponentsPage.clickOnCreateButton();
    vocabularyUpdatePage = new VocabularyUpdatePage();
    expect(await vocabularyUpdatePage.getPageTitle()).to.eq('cvsApp.vocabulary.home.createOrEditLabel');
    await vocabularyUpdatePage.cancel();
  });

  it('should create and save Vocabularies', async () => {
    const nbButtonsBeforeCreate = await vocabularyComponentsPage.countDeleteButtons();

    await vocabularyComponentsPage.clickOnCreateButton();

    await promise.all([
      vocabularyUpdatePage.setStatusInput('status'),
      vocabularyUpdatePage.setUriInput('uri'),
      vocabularyUpdatePage.setNotationInput('notation'),
      vocabularyUpdatePage.setVersionNumberInput('versionNumber'),
      vocabularyUpdatePage.setInitialPublicationInput('5'),
      vocabularyUpdatePage.setPreviousPublicationInput('5'),
      vocabularyUpdatePage.setSourceLanguageInput('sourceLanguage'),
      vocabularyUpdatePage.setAgencyIdInput('5'),
      vocabularyUpdatePage.setAgencyNameInput('agencyName'),
      vocabularyUpdatePage.setAgencyLogoInput('agencyLogo'),
      vocabularyUpdatePage.setPublicationDateInput('2000-12-31'),
      vocabularyUpdatePage.setLastModifiedInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
      vocabularyUpdatePage.setNotesInput('notes'),
      vocabularyUpdatePage.setVersionSqInput('versionSq'),
      vocabularyUpdatePage.setTitleSqInput('titleSq'),
      vocabularyUpdatePage.setDefinitionSqInput('definitionSq'),
      vocabularyUpdatePage.setVersionBsInput('versionBs'),
      vocabularyUpdatePage.setTitleBsInput('titleBs'),
      vocabularyUpdatePage.setDefinitionBsInput('definitionBs'),
      vocabularyUpdatePage.setVersionBgInput('versionBg'),
      vocabularyUpdatePage.setTitleBgInput('titleBg'),
      vocabularyUpdatePage.setDefinitionBgInput('definitionBg'),
      vocabularyUpdatePage.setVersionHrInput('versionHr'),
      vocabularyUpdatePage.setTitleHrInput('titleHr'),
      vocabularyUpdatePage.setDefinitionHrInput('definitionHr'),
      vocabularyUpdatePage.setVersionCsInput('versionCs'),
      vocabularyUpdatePage.setTitleCsInput('titleCs'),
      vocabularyUpdatePage.setDefinitionCsInput('definitionCs'),
      vocabularyUpdatePage.setVersionDaInput('versionDa'),
      vocabularyUpdatePage.setTitleDaInput('titleDa'),
      vocabularyUpdatePage.setDefinitionDaInput('definitionDa'),
      vocabularyUpdatePage.setVersionNlInput('versionNl'),
      vocabularyUpdatePage.setTitleNlInput('titleNl'),
      vocabularyUpdatePage.setDefinitionNlInput('definitionNl'),
      vocabularyUpdatePage.setVersionEnInput('versionEn'),
      vocabularyUpdatePage.setTitleEnInput('titleEn'),
      vocabularyUpdatePage.setDefinitionEnInput('definitionEn'),
      vocabularyUpdatePage.setVersionEtInput('versionEt'),
      vocabularyUpdatePage.setTitleEtInput('titleEt'),
      vocabularyUpdatePage.setDefinitionEtInput('definitionEt'),
      vocabularyUpdatePage.setVersionFiInput('versionFi'),
      vocabularyUpdatePage.setTitleFiInput('titleFi'),
      vocabularyUpdatePage.setDefinitionFiInput('definitionFi'),
      vocabularyUpdatePage.setVersionFrInput('versionFr'),
      vocabularyUpdatePage.setTitleFrInput('titleFr'),
      vocabularyUpdatePage.setDefinitionFrInput('definitionFr'),
      vocabularyUpdatePage.setVersionDeInput('versionDe'),
      vocabularyUpdatePage.setTitleDeInput('titleDe'),
      vocabularyUpdatePage.setDefinitionDeInput('definitionDe'),
      vocabularyUpdatePage.setVersionElInput('versionEl'),
      vocabularyUpdatePage.setTitleElInput('titleEl'),
      vocabularyUpdatePage.setDefinitionElInput('definitionEl'),
      vocabularyUpdatePage.setVersionHuInput('versionHu'),
      vocabularyUpdatePage.setTitleHuInput('titleHu'),
      vocabularyUpdatePage.setDefinitionHuInput('definitionHu'),
      vocabularyUpdatePage.setVersionItInput('versionIt'),
      vocabularyUpdatePage.setTitleItInput('titleIt'),
      vocabularyUpdatePage.setDefinitionItInput('definitionIt'),
      vocabularyUpdatePage.setVersionJaInput('versionJa'),
      vocabularyUpdatePage.setTitleJaInput('titleJa'),
      vocabularyUpdatePage.setDefinitionJaInput('definitionJa'),
      vocabularyUpdatePage.setVersionLtInput('versionLt'),
      vocabularyUpdatePage.setTitleLtInput('titleLt'),
      vocabularyUpdatePage.setDefinitionLtInput('definitionLt'),
      vocabularyUpdatePage.setVersionMkInput('versionMk'),
      vocabularyUpdatePage.setTitleMkInput('titleMk'),
      vocabularyUpdatePage.setDefinitionMkInput('definitionMk'),
      vocabularyUpdatePage.setVersionNoInput('versionNo'),
      vocabularyUpdatePage.setTitleNoInput('titleNo'),
      vocabularyUpdatePage.setDefinitionNoInput('definitionNo'),
      vocabularyUpdatePage.setVersionPlInput('versionPl'),
      vocabularyUpdatePage.setTitlePlInput('titlePl'),
      vocabularyUpdatePage.setDefinitionPlInput('definitionPl'),
      vocabularyUpdatePage.setVersionPtInput('versionPt'),
      vocabularyUpdatePage.setTitlePtInput('titlePt'),
      vocabularyUpdatePage.setDefinitionPtInput('definitionPt'),
      vocabularyUpdatePage.setVersionRoInput('versionRo'),
      vocabularyUpdatePage.setTitleRoInput('titleRo'),
      vocabularyUpdatePage.setDefinitionRoInput('definitionRo'),
      vocabularyUpdatePage.setVersionRuInput('versionRu'),
      vocabularyUpdatePage.setTitleRuInput('titleRu'),
      vocabularyUpdatePage.setDefinitionRuInput('definitionRu'),
      vocabularyUpdatePage.setVersionSrInput('versionSr'),
      vocabularyUpdatePage.setTitleSrInput('titleSr'),
      vocabularyUpdatePage.setDefinitionSrInput('definitionSr'),
      vocabularyUpdatePage.setVersionSkInput('versionSk'),
      vocabularyUpdatePage.setTitleSkInput('titleSk'),
      vocabularyUpdatePage.setDefinitionSkInput('definitionSk'),
      vocabularyUpdatePage.setVersionSlInput('versionSl'),
      vocabularyUpdatePage.setTitleSlInput('titleSl'),
      vocabularyUpdatePage.setDefinitionSlInput('definitionSl'),
      vocabularyUpdatePage.setVersionEsInput('versionEs'),
      vocabularyUpdatePage.setTitleEsInput('titleEs'),
      vocabularyUpdatePage.setDefinitionEsInput('definitionEs'),
      vocabularyUpdatePage.setVersionSvInput('versionSv'),
      vocabularyUpdatePage.setTitleSvInput('titleSv'),
      vocabularyUpdatePage.setDefinitionSvInput('definitionSv')
    ]);

    expect(await vocabularyUpdatePage.getStatusInput()).to.eq('status', 'Expected Status value to be equals to status');
    expect(await vocabularyUpdatePage.getUriInput()).to.eq('uri', 'Expected Uri value to be equals to uri');
    expect(await vocabularyUpdatePage.getNotationInput()).to.eq('notation', 'Expected Notation value to be equals to notation');
    expect(await vocabularyUpdatePage.getVersionNumberInput()).to.eq(
      'versionNumber',
      'Expected VersionNumber value to be equals to versionNumber'
    );
    expect(await vocabularyUpdatePage.getInitialPublicationInput()).to.eq('5', 'Expected initialPublication value to be equals to 5');
    expect(await vocabularyUpdatePage.getPreviousPublicationInput()).to.eq('5', 'Expected previousPublication value to be equals to 5');
    const selectedArchived = vocabularyUpdatePage.getArchivedInput();
    if (await selectedArchived.isSelected()) {
      await vocabularyUpdatePage.getArchivedInput().click();
      expect(await vocabularyUpdatePage.getArchivedInput().isSelected(), 'Expected archived not to be selected').to.be.false;
    } else {
      await vocabularyUpdatePage.getArchivedInput().click();
      expect(await vocabularyUpdatePage.getArchivedInput().isSelected(), 'Expected archived to be selected').to.be.true;
    }
    const selectedWithdrawn = vocabularyUpdatePage.getWithdrawnInput();
    if (await selectedWithdrawn.isSelected()) {
      await vocabularyUpdatePage.getWithdrawnInput().click();
      expect(await vocabularyUpdatePage.getWithdrawnInput().isSelected(), 'Expected withdrawn not to be selected').to.be.false;
    } else {
      await vocabularyUpdatePage.getWithdrawnInput().click();
      expect(await vocabularyUpdatePage.getWithdrawnInput().isSelected(), 'Expected withdrawn to be selected').to.be.true;
    }
    const selectedDiscoverable = vocabularyUpdatePage.getDiscoverableInput();
    if (await selectedDiscoverable.isSelected()) {
      await vocabularyUpdatePage.getDiscoverableInput().click();
      expect(await vocabularyUpdatePage.getDiscoverableInput().isSelected(), 'Expected discoverable not to be selected').to.be.false;
    } else {
      await vocabularyUpdatePage.getDiscoverableInput().click();
      expect(await vocabularyUpdatePage.getDiscoverableInput().isSelected(), 'Expected discoverable to be selected').to.be.true;
    }
    expect(await vocabularyUpdatePage.getSourceLanguageInput()).to.eq(
      'sourceLanguage',
      'Expected SourceLanguage value to be equals to sourceLanguage'
    );
    expect(await vocabularyUpdatePage.getAgencyIdInput()).to.eq('5', 'Expected agencyId value to be equals to 5');
    expect(await vocabularyUpdatePage.getAgencyNameInput()).to.eq('agencyName', 'Expected AgencyName value to be equals to agencyName');
    expect(await vocabularyUpdatePage.getAgencyLogoInput()).to.eq('agencyLogo', 'Expected AgencyLogo value to be equals to agencyLogo');
    expect(await vocabularyUpdatePage.getPublicationDateInput()).to.eq(
      '2000-12-31',
      'Expected publicationDate value to be equals to 2000-12-31'
    );
    expect(await vocabularyUpdatePage.getLastModifiedInput()).to.contain(
      '2001-01-01T02:30',
      'Expected lastModified value to be equals to 2000-12-31'
    );
    expect(await vocabularyUpdatePage.getNotesInput()).to.eq('notes', 'Expected Notes value to be equals to notes');
    expect(await vocabularyUpdatePage.getVersionSqInput()).to.eq('versionSq', 'Expected VersionSq value to be equals to versionSq');
    expect(await vocabularyUpdatePage.getTitleSqInput()).to.eq('titleSq', 'Expected TitleSq value to be equals to titleSq');
    expect(await vocabularyUpdatePage.getDefinitionSqInput()).to.eq(
      'definitionSq',
      'Expected DefinitionSq value to be equals to definitionSq'
    );
    expect(await vocabularyUpdatePage.getVersionBsInput()).to.eq('versionBs', 'Expected VersionBs value to be equals to versionBs');
    expect(await vocabularyUpdatePage.getTitleBsInput()).to.eq('titleBs', 'Expected TitleBs value to be equals to titleBs');
    expect(await vocabularyUpdatePage.getDefinitionBsInput()).to.eq(
      'definitionBs',
      'Expected DefinitionBs value to be equals to definitionBs'
    );
    expect(await vocabularyUpdatePage.getVersionBgInput()).to.eq('versionBg', 'Expected VersionBg value to be equals to versionBg');
    expect(await vocabularyUpdatePage.getTitleBgInput()).to.eq('titleBg', 'Expected TitleBg value to be equals to titleBg');
    expect(await vocabularyUpdatePage.getDefinitionBgInput()).to.eq(
      'definitionBg',
      'Expected DefinitionBg value to be equals to definitionBg'
    );
    expect(await vocabularyUpdatePage.getVersionHrInput()).to.eq('versionHr', 'Expected VersionHr value to be equals to versionHr');
    expect(await vocabularyUpdatePage.getTitleHrInput()).to.eq('titleHr', 'Expected TitleHr value to be equals to titleHr');
    expect(await vocabularyUpdatePage.getDefinitionHrInput()).to.eq(
      'definitionHr',
      'Expected DefinitionHr value to be equals to definitionHr'
    );
    expect(await vocabularyUpdatePage.getVersionCsInput()).to.eq('versionCs', 'Expected VersionCs value to be equals to versionCs');
    expect(await vocabularyUpdatePage.getTitleCsInput()).to.eq('titleCs', 'Expected TitleCs value to be equals to titleCs');
    expect(await vocabularyUpdatePage.getDefinitionCsInput()).to.eq(
      'definitionCs',
      'Expected DefinitionCs value to be equals to definitionCs'
    );
    expect(await vocabularyUpdatePage.getVersionDaInput()).to.eq('versionDa', 'Expected VersionDa value to be equals to versionDa');
    expect(await vocabularyUpdatePage.getTitleDaInput()).to.eq('titleDa', 'Expected TitleDa value to be equals to titleDa');
    expect(await vocabularyUpdatePage.getDefinitionDaInput()).to.eq(
      'definitionDa',
      'Expected DefinitionDa value to be equals to definitionDa'
    );
    expect(await vocabularyUpdatePage.getVersionNlInput()).to.eq('versionNl', 'Expected VersionNl value to be equals to versionNl');
    expect(await vocabularyUpdatePage.getTitleNlInput()).to.eq('titleNl', 'Expected TitleNl value to be equals to titleNl');
    expect(await vocabularyUpdatePage.getDefinitionNlInput()).to.eq(
      'definitionNl',
      'Expected DefinitionNl value to be equals to definitionNl'
    );
    expect(await vocabularyUpdatePage.getVersionEnInput()).to.eq('versionEn', 'Expected VersionEn value to be equals to versionEn');
    expect(await vocabularyUpdatePage.getTitleEnInput()).to.eq('titleEn', 'Expected TitleEn value to be equals to titleEn');
    expect(await vocabularyUpdatePage.getDefinitionEnInput()).to.eq(
      'definitionEn',
      'Expected DefinitionEn value to be equals to definitionEn'
    );
    expect(await vocabularyUpdatePage.getVersionEtInput()).to.eq('versionEt', 'Expected VersionEt value to be equals to versionEt');
    expect(await vocabularyUpdatePage.getTitleEtInput()).to.eq('titleEt', 'Expected TitleEt value to be equals to titleEt');
    expect(await vocabularyUpdatePage.getDefinitionEtInput()).to.eq(
      'definitionEt',
      'Expected DefinitionEt value to be equals to definitionEt'
    );
    expect(await vocabularyUpdatePage.getVersionFiInput()).to.eq('versionFi', 'Expected VersionFi value to be equals to versionFi');
    expect(await vocabularyUpdatePage.getTitleFiInput()).to.eq('titleFi', 'Expected TitleFi value to be equals to titleFi');
    expect(await vocabularyUpdatePage.getDefinitionFiInput()).to.eq(
      'definitionFi',
      'Expected DefinitionFi value to be equals to definitionFi'
    );
    expect(await vocabularyUpdatePage.getVersionFrInput()).to.eq('versionFr', 'Expected VersionFr value to be equals to versionFr');
    expect(await vocabularyUpdatePage.getTitleFrInput()).to.eq('titleFr', 'Expected TitleFr value to be equals to titleFr');
    expect(await vocabularyUpdatePage.getDefinitionFrInput()).to.eq(
      'definitionFr',
      'Expected DefinitionFr value to be equals to definitionFr'
    );
    expect(await vocabularyUpdatePage.getVersionDeInput()).to.eq('versionDe', 'Expected VersionDe value to be equals to versionDe');
    expect(await vocabularyUpdatePage.getTitleDeInput()).to.eq('titleDe', 'Expected TitleDe value to be equals to titleDe');
    expect(await vocabularyUpdatePage.getDefinitionDeInput()).to.eq(
      'definitionDe',
      'Expected DefinitionDe value to be equals to definitionDe'
    );
    expect(await vocabularyUpdatePage.getVersionElInput()).to.eq('versionEl', 'Expected VersionEl value to be equals to versionEl');
    expect(await vocabularyUpdatePage.getTitleElInput()).to.eq('titleEl', 'Expected TitleEl value to be equals to titleEl');
    expect(await vocabularyUpdatePage.getDefinitionElInput()).to.eq(
      'definitionEl',
      'Expected DefinitionEl value to be equals to definitionEl'
    );
    expect(await vocabularyUpdatePage.getVersionHuInput()).to.eq('versionHu', 'Expected VersionHu value to be equals to versionHu');
    expect(await vocabularyUpdatePage.getTitleHuInput()).to.eq('titleHu', 'Expected TitleHu value to be equals to titleHu');
    expect(await vocabularyUpdatePage.getDefinitionHuInput()).to.eq(
      'definitionHu',
      'Expected DefinitionHu value to be equals to definitionHu'
    );
    expect(await vocabularyUpdatePage.getVersionItInput()).to.eq('versionIt', 'Expected VersionIt value to be equals to versionIt');
    expect(await vocabularyUpdatePage.getTitleItInput()).to.eq('titleIt', 'Expected TitleIt value to be equals to titleIt');
    expect(await vocabularyUpdatePage.getDefinitionItInput()).to.eq(
      'definitionIt',
      'Expected DefinitionIt value to be equals to definitionIt'
    );
    expect(await vocabularyUpdatePage.getVersionJaInput()).to.eq('versionJa', 'Expected VersionJa value to be equals to versionJa');
    expect(await vocabularyUpdatePage.getTitleJaInput()).to.eq('titleJa', 'Expected TitleJa value to be equals to titleJa');
    expect(await vocabularyUpdatePage.getDefinitionJaInput()).to.eq(
      'definitionJa',
      'Expected DefinitionJa value to be equals to definitionJa'
    );
    expect(await vocabularyUpdatePage.getVersionLtInput()).to.eq('versionLt', 'Expected VersionLt value to be equals to versionLt');
    expect(await vocabularyUpdatePage.getTitleLtInput()).to.eq('titleLt', 'Expected TitleLt value to be equals to titleLt');
    expect(await vocabularyUpdatePage.getDefinitionLtInput()).to.eq(
      'definitionLt',
      'Expected DefinitionLt value to be equals to definitionLt'
    );
    expect(await vocabularyUpdatePage.getVersionMkInput()).to.eq('versionMk', 'Expected VersionMk value to be equals to versionMk');
    expect(await vocabularyUpdatePage.getTitleMkInput()).to.eq('titleMk', 'Expected TitleMk value to be equals to titleMk');
    expect(await vocabularyUpdatePage.getDefinitionMkInput()).to.eq(
      'definitionMk',
      'Expected DefinitionMk value to be equals to definitionMk'
    );
    expect(await vocabularyUpdatePage.getVersionNoInput()).to.eq('versionNo', 'Expected VersionNo value to be equals to versionNo');
    expect(await vocabularyUpdatePage.getTitleNoInput()).to.eq('titleNo', 'Expected TitleNo value to be equals to titleNo');
    expect(await vocabularyUpdatePage.getDefinitionNoInput()).to.eq(
      'definitionNo',
      'Expected DefinitionNo value to be equals to definitionNo'
    );
    expect(await vocabularyUpdatePage.getVersionPlInput()).to.eq('versionPl', 'Expected VersionPl value to be equals to versionPl');
    expect(await vocabularyUpdatePage.getTitlePlInput()).to.eq('titlePl', 'Expected TitlePl value to be equals to titlePl');
    expect(await vocabularyUpdatePage.getDefinitionPlInput()).to.eq(
      'definitionPl',
      'Expected DefinitionPl value to be equals to definitionPl'
    );
    expect(await vocabularyUpdatePage.getVersionPtInput()).to.eq('versionPt', 'Expected VersionPt value to be equals to versionPt');
    expect(await vocabularyUpdatePage.getTitlePtInput()).to.eq('titlePt', 'Expected TitlePt value to be equals to titlePt');
    expect(await vocabularyUpdatePage.getDefinitionPtInput()).to.eq(
      'definitionPt',
      'Expected DefinitionPt value to be equals to definitionPt'
    );
    expect(await vocabularyUpdatePage.getVersionRoInput()).to.eq('versionRo', 'Expected VersionRo value to be equals to versionRo');
    expect(await vocabularyUpdatePage.getTitleRoInput()).to.eq('titleRo', 'Expected TitleRo value to be equals to titleRo');
    expect(await vocabularyUpdatePage.getDefinitionRoInput()).to.eq(
      'definitionRo',
      'Expected DefinitionRo value to be equals to definitionRo'
    );
    expect(await vocabularyUpdatePage.getVersionRuInput()).to.eq('versionRu', 'Expected VersionRu value to be equals to versionRu');
    expect(await vocabularyUpdatePage.getTitleRuInput()).to.eq('titleRu', 'Expected TitleRu value to be equals to titleRu');
    expect(await vocabularyUpdatePage.getDefinitionRuInput()).to.eq(
      'definitionRu',
      'Expected DefinitionRu value to be equals to definitionRu'
    );
    expect(await vocabularyUpdatePage.getVersionSrInput()).to.eq('versionSr', 'Expected VersionSr value to be equals to versionSr');
    expect(await vocabularyUpdatePage.getTitleSrInput()).to.eq('titleSr', 'Expected TitleSr value to be equals to titleSr');
    expect(await vocabularyUpdatePage.getDefinitionSrInput()).to.eq(
      'definitionSr',
      'Expected DefinitionSr value to be equals to definitionSr'
    );
    expect(await vocabularyUpdatePage.getVersionSkInput()).to.eq('versionSk', 'Expected VersionSk value to be equals to versionSk');
    expect(await vocabularyUpdatePage.getTitleSkInput()).to.eq('titleSk', 'Expected TitleSk value to be equals to titleSk');
    expect(await vocabularyUpdatePage.getDefinitionSkInput()).to.eq(
      'definitionSk',
      'Expected DefinitionSk value to be equals to definitionSk'
    );
    expect(await vocabularyUpdatePage.getVersionSlInput()).to.eq('versionSl', 'Expected VersionSl value to be equals to versionSl');
    expect(await vocabularyUpdatePage.getTitleSlInput()).to.eq('titleSl', 'Expected TitleSl value to be equals to titleSl');
    expect(await vocabularyUpdatePage.getDefinitionSlInput()).to.eq(
      'definitionSl',
      'Expected DefinitionSl value to be equals to definitionSl'
    );
    expect(await vocabularyUpdatePage.getVersionEsInput()).to.eq('versionEs', 'Expected VersionEs value to be equals to versionEs');
    expect(await vocabularyUpdatePage.getTitleEsInput()).to.eq('titleEs', 'Expected TitleEs value to be equals to titleEs');
    expect(await vocabularyUpdatePage.getDefinitionEsInput()).to.eq(
      'definitionEs',
      'Expected DefinitionEs value to be equals to definitionEs'
    );
    expect(await vocabularyUpdatePage.getVersionSvInput()).to.eq('versionSv', 'Expected VersionSv value to be equals to versionSv');
    expect(await vocabularyUpdatePage.getTitleSvInput()).to.eq('titleSv', 'Expected TitleSv value to be equals to titleSv');
    expect(await vocabularyUpdatePage.getDefinitionSvInput()).to.eq(
      'definitionSv',
      'Expected DefinitionSv value to be equals to definitionSv'
    );

    await vocabularyUpdatePage.save();
    expect(await vocabularyUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await vocabularyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Vocabulary', async () => {
    const nbButtonsBeforeDelete = await vocabularyComponentsPage.countDeleteButtons();
    await vocabularyComponentsPage.clickOnLastDeleteButton();

    vocabularyDeleteDialog = new VocabularyDeleteDialog();
    expect(await vocabularyDeleteDialog.getDialogTitle()).to.eq('cvsApp.vocabulary.delete.question');
    await vocabularyDeleteDialog.clickOnConfirmButton();

    expect(await vocabularyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
