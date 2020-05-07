import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { VersionComponentsPage, VersionDeleteDialog, VersionUpdatePage } from './version.page-object';

const expect = chai.expect;

describe('Version e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let versionComponentsPage: VersionComponentsPage;
  let versionUpdatePage: VersionUpdatePage;
  let versionDeleteDialog: VersionDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Versions', async () => {
    await navBarPage.goToEntity('version');
    versionComponentsPage = new VersionComponentsPage();
    await browser.wait(ec.visibilityOf(versionComponentsPage.title), 5000);
    expect(await versionComponentsPage.getTitle()).to.eq('cvsApp.version.home.title');
    await browser.wait(ec.or(ec.visibilityOf(versionComponentsPage.entities), ec.visibilityOf(versionComponentsPage.noResult)), 1000);
  });

  it('should load create Version page', async () => {
    await versionComponentsPage.clickOnCreateButton();
    versionUpdatePage = new VersionUpdatePage();
    expect(await versionUpdatePage.getPageTitle()).to.eq('cvsApp.version.home.createOrEditLabel');
    await versionUpdatePage.cancel();
  });

  it('should create and save Versions', async () => {
    const nbButtonsBeforeCreate = await versionComponentsPage.countDeleteButtons();

    await versionComponentsPage.clickOnCreateButton();

    await promise.all([
      versionUpdatePage.setStatusInput('status'),
      versionUpdatePage.setItemTypeInput('itemType'),
      versionUpdatePage.setLanguageInput('language'),
      versionUpdatePage.setPublicationDateInput('2000-12-31'),
      versionUpdatePage.setLastModifiedInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
      versionUpdatePage.setNumberInput('number'),
      versionUpdatePage.setUriInput('uri'),
      versionUpdatePage.setCanonicalUriInput('canonicalUri'),
      versionUpdatePage.setUriSlInput('uriSl'),
      versionUpdatePage.setNotationInput('notation'),
      versionUpdatePage.setTitleInput('title'),
      versionUpdatePage.setDefinitionInput('definition'),
      versionUpdatePage.setPreviousVersionInput('5'),
      versionUpdatePage.setInitialVersionInput('5'),
      versionUpdatePage.setCreatorInput('5'),
      versionUpdatePage.setPublisherInput('5'),
      versionUpdatePage.setNotesInput('notes'),
      versionUpdatePage.setVersionNotesInput('versionNotes'),
      versionUpdatePage.setVersionChangesInput('versionChanges'),
      versionUpdatePage.setDiscussionNotesInput('discussionNotes'),
      versionUpdatePage.setLicenseInput('license'),
      versionUpdatePage.setLicenseIdInput('5'),
      versionUpdatePage.setCitationInput('citation'),
      versionUpdatePage.setDdiUsageInput('ddiUsage'),
      versionUpdatePage.setTranslateAgencyInput('translateAgency'),
      versionUpdatePage.setTranslateAgencyLinkInput('translateAgencyLink'),
      versionUpdatePage.vocabularySelectLastOption()
    ]);

    expect(await versionUpdatePage.getStatusInput()).to.eq('status', 'Expected Status value to be equals to status');
    expect(await versionUpdatePage.getItemTypeInput()).to.eq('itemType', 'Expected ItemType value to be equals to itemType');
    expect(await versionUpdatePage.getLanguageInput()).to.eq('language', 'Expected Language value to be equals to language');
    expect(await versionUpdatePage.getPublicationDateInput()).to.eq(
      '2000-12-31',
      'Expected publicationDate value to be equals to 2000-12-31'
    );
    expect(await versionUpdatePage.getLastModifiedInput()).to.contain(
      '2001-01-01T02:30',
      'Expected lastModified value to be equals to 2000-12-31'
    );
    expect(await versionUpdatePage.getNumberInput()).to.eq('number', 'Expected Number value to be equals to number');
    expect(await versionUpdatePage.getUriInput()).to.eq('uri', 'Expected Uri value to be equals to uri');
    expect(await versionUpdatePage.getCanonicalUriInput()).to.eq(
      'canonicalUri',
      'Expected CanonicalUri value to be equals to canonicalUri'
    );
    expect(await versionUpdatePage.getUriSlInput()).to.eq('uriSl', 'Expected UriSl value to be equals to uriSl');
    expect(await versionUpdatePage.getNotationInput()).to.eq('notation', 'Expected Notation value to be equals to notation');
    expect(await versionUpdatePage.getTitleInput()).to.eq('title', 'Expected Title value to be equals to title');
    expect(await versionUpdatePage.getDefinitionInput()).to.eq('definition', 'Expected Definition value to be equals to definition');
    expect(await versionUpdatePage.getPreviousVersionInput()).to.eq('5', 'Expected previousVersion value to be equals to 5');
    expect(await versionUpdatePage.getInitialVersionInput()).to.eq('5', 'Expected initialVersion value to be equals to 5');
    expect(await versionUpdatePage.getCreatorInput()).to.eq('5', 'Expected creator value to be equals to 5');
    expect(await versionUpdatePage.getPublisherInput()).to.eq('5', 'Expected publisher value to be equals to 5');
    expect(await versionUpdatePage.getNotesInput()).to.eq('notes', 'Expected Notes value to be equals to notes');
    expect(await versionUpdatePage.getVersionNotesInput()).to.eq(
      'versionNotes',
      'Expected VersionNotes value to be equals to versionNotes'
    );
    expect(await versionUpdatePage.getVersionChangesInput()).to.eq(
      'versionChanges',
      'Expected VersionChanges value to be equals to versionChanges'
    );
    expect(await versionUpdatePage.getDiscussionNotesInput()).to.eq(
      'discussionNotes',
      'Expected DiscussionNotes value to be equals to discussionNotes'
    );
    expect(await versionUpdatePage.getLicenseInput()).to.eq('license', 'Expected License value to be equals to license');
    expect(await versionUpdatePage.getLicenseIdInput()).to.eq('5', 'Expected licenseId value to be equals to 5');
    expect(await versionUpdatePage.getCitationInput()).to.eq('citation', 'Expected Citation value to be equals to citation');
    expect(await versionUpdatePage.getDdiUsageInput()).to.eq('ddiUsage', 'Expected DdiUsage value to be equals to ddiUsage');
    expect(await versionUpdatePage.getTranslateAgencyInput()).to.eq(
      'translateAgency',
      'Expected TranslateAgency value to be equals to translateAgency'
    );
    expect(await versionUpdatePage.getTranslateAgencyLinkInput()).to.eq(
      'translateAgencyLink',
      'Expected TranslateAgencyLink value to be equals to translateAgencyLink'
    );

    await versionUpdatePage.save();
    expect(await versionUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await versionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Version', async () => {
    const nbButtonsBeforeDelete = await versionComponentsPage.countDeleteButtons();
    await versionComponentsPage.clickOnLastDeleteButton();

    versionDeleteDialog = new VersionDeleteDialog();
    expect(await versionDeleteDialog.getDialogTitle()).to.eq('cvsApp.version.delete.question');
    await versionDeleteDialog.clickOnConfirmButton();

    expect(await versionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
