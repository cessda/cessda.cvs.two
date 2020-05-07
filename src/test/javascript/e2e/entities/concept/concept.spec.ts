import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { ConceptComponentsPage, ConceptDeleteDialog, ConceptUpdatePage } from './concept.page-object';

const expect = chai.expect;

describe('Concept e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let conceptComponentsPage: ConceptComponentsPage;
  let conceptUpdatePage: ConceptUpdatePage;
  let conceptDeleteDialog: ConceptDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Concepts', async () => {
    await navBarPage.goToEntity('concept');
    conceptComponentsPage = new ConceptComponentsPage();
    await browser.wait(ec.visibilityOf(conceptComponentsPage.title), 5000);
    expect(await conceptComponentsPage.getTitle()).to.eq('cvsApp.concept.home.title');
    await browser.wait(ec.or(ec.visibilityOf(conceptComponentsPage.entities), ec.visibilityOf(conceptComponentsPage.noResult)), 1000);
  });

  it('should load create Concept page', async () => {
    await conceptComponentsPage.clickOnCreateButton();
    conceptUpdatePage = new ConceptUpdatePage();
    expect(await conceptUpdatePage.getPageTitle()).to.eq('cvsApp.concept.home.createOrEditLabel');
    await conceptUpdatePage.cancel();
  });

  it('should create and save Concepts', async () => {
    const nbButtonsBeforeCreate = await conceptComponentsPage.countDeleteButtons();

    await conceptComponentsPage.clickOnCreateButton();

    await promise.all([
      conceptUpdatePage.setUriInput('uri'),
      conceptUpdatePage.setNotationInput('notation'),
      conceptUpdatePage.setTitleInput('title'),
      conceptUpdatePage.setDefinitionInput('definition'),
      conceptUpdatePage.setPreviousConceptInput('5'),
      conceptUpdatePage.setSlConceptInput('5'),
      conceptUpdatePage.setParentInput('parent'),
      conceptUpdatePage.setPositionInput('5'),
      conceptUpdatePage.versionSelectLastOption()
    ]);

    expect(await conceptUpdatePage.getUriInput()).to.eq('uri', 'Expected Uri value to be equals to uri');
    expect(await conceptUpdatePage.getNotationInput()).to.eq('notation', 'Expected Notation value to be equals to notation');
    expect(await conceptUpdatePage.getTitleInput()).to.eq('title', 'Expected Title value to be equals to title');
    expect(await conceptUpdatePage.getDefinitionInput()).to.eq('definition', 'Expected Definition value to be equals to definition');
    expect(await conceptUpdatePage.getPreviousConceptInput()).to.eq('5', 'Expected previousConcept value to be equals to 5');
    expect(await conceptUpdatePage.getSlConceptInput()).to.eq('5', 'Expected slConcept value to be equals to 5');
    expect(await conceptUpdatePage.getParentInput()).to.eq('parent', 'Expected Parent value to be equals to parent');
    expect(await conceptUpdatePage.getPositionInput()).to.eq('5', 'Expected position value to be equals to 5');

    await conceptUpdatePage.save();
    expect(await conceptUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await conceptComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Concept', async () => {
    const nbButtonsBeforeDelete = await conceptComponentsPage.countDeleteButtons();
    await conceptComponentsPage.clickOnLastDeleteButton();

    conceptDeleteDialog = new ConceptDeleteDialog();
    expect(await conceptDeleteDialog.getDialogTitle()).to.eq('cvsApp.concept.delete.question');
    await conceptDeleteDialog.clickOnConfirmButton();

    expect(await conceptComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
