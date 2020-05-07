import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { VocabularyChangeComponentsPage, VocabularyChangeDeleteDialog, VocabularyChangeUpdatePage } from './vocabulary-change.page-object';

const expect = chai.expect;

describe('VocabularyChange e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let vocabularyChangeComponentsPage: VocabularyChangeComponentsPage;
  let vocabularyChangeUpdatePage: VocabularyChangeUpdatePage;
  let vocabularyChangeDeleteDialog: VocabularyChangeDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load VocabularyChanges', async () => {
    await navBarPage.goToEntity('vocabulary-change');
    vocabularyChangeComponentsPage = new VocabularyChangeComponentsPage();
    await browser.wait(ec.visibilityOf(vocabularyChangeComponentsPage.title), 5000);
    expect(await vocabularyChangeComponentsPage.getTitle()).to.eq('cvsApp.vocabularyChange.home.title');
    await browser.wait(
      ec.or(ec.visibilityOf(vocabularyChangeComponentsPage.entities), ec.visibilityOf(vocabularyChangeComponentsPage.noResult)),
      1000
    );
  });

  it('should load create VocabularyChange page', async () => {
    await vocabularyChangeComponentsPage.clickOnCreateButton();
    vocabularyChangeUpdatePage = new VocabularyChangeUpdatePage();
    expect(await vocabularyChangeUpdatePage.getPageTitle()).to.eq('cvsApp.vocabularyChange.home.createOrEditLabel');
    await vocabularyChangeUpdatePage.cancel();
  });

  it('should create and save VocabularyChanges', async () => {
    const nbButtonsBeforeCreate = await vocabularyChangeComponentsPage.countDeleteButtons();

    await vocabularyChangeComponentsPage.clickOnCreateButton();

    await promise.all([
      vocabularyChangeUpdatePage.setVocabularyIdInput('5'),
      vocabularyChangeUpdatePage.setVersionIdInput('5'),
      vocabularyChangeUpdatePage.setChangeTypeInput('changeType'),
      vocabularyChangeUpdatePage.setDescriptionInput('description'),
      vocabularyChangeUpdatePage.setUserIdInput('5'),
      vocabularyChangeUpdatePage.setUserNameInput('userName'),
      vocabularyChangeUpdatePage.setDateInput('2000-12-31')
    ]);

    expect(await vocabularyChangeUpdatePage.getVocabularyIdInput()).to.eq('5', 'Expected vocabularyId value to be equals to 5');
    expect(await vocabularyChangeUpdatePage.getVersionIdInput()).to.eq('5', 'Expected versionId value to be equals to 5');
    expect(await vocabularyChangeUpdatePage.getChangeTypeInput()).to.eq(
      'changeType',
      'Expected ChangeType value to be equals to changeType'
    );
    expect(await vocabularyChangeUpdatePage.getDescriptionInput()).to.eq(
      'description',
      'Expected Description value to be equals to description'
    );
    expect(await vocabularyChangeUpdatePage.getUserIdInput()).to.eq('5', 'Expected userId value to be equals to 5');
    expect(await vocabularyChangeUpdatePage.getUserNameInput()).to.eq('userName', 'Expected UserName value to be equals to userName');
    expect(await vocabularyChangeUpdatePage.getDateInput()).to.eq('2000-12-31', 'Expected date value to be equals to 2000-12-31');

    await vocabularyChangeUpdatePage.save();
    expect(await vocabularyChangeUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await vocabularyChangeComponentsPage.countDeleteButtons()).to.eq(
      nbButtonsBeforeCreate + 1,
      'Expected one more entry in the table'
    );
  });

  it('should delete last VocabularyChange', async () => {
    const nbButtonsBeforeDelete = await vocabularyChangeComponentsPage.countDeleteButtons();
    await vocabularyChangeComponentsPage.clickOnLastDeleteButton();

    vocabularyChangeDeleteDialog = new VocabularyChangeDeleteDialog();
    expect(await vocabularyChangeDeleteDialog.getDialogTitle()).to.eq('cvsApp.vocabularyChange.delete.question');
    await vocabularyChangeDeleteDialog.clickOnConfirmButton();

    expect(await vocabularyChangeComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
