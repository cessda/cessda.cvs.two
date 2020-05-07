import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { AgencyComponentsPage, AgencyDeleteDialog, AgencyUpdatePage } from './agency.page-object';

const expect = chai.expect;

describe('Agency e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let agencyComponentsPage: AgencyComponentsPage;
  let agencyUpdatePage: AgencyUpdatePage;
  let agencyDeleteDialog: AgencyDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Agencies', async () => {
    await navBarPage.goToEntity('agency');
    agencyComponentsPage = new AgencyComponentsPage();
    await browser.wait(ec.visibilityOf(agencyComponentsPage.title), 5000);
    expect(await agencyComponentsPage.getTitle()).to.eq('cvsApp.agency.home.title');
    await browser.wait(ec.or(ec.visibilityOf(agencyComponentsPage.entities), ec.visibilityOf(agencyComponentsPage.noResult)), 1000);
  });

  it('should load create Agency page', async () => {
    await agencyComponentsPage.clickOnCreateButton();
    agencyUpdatePage = new AgencyUpdatePage();
    expect(await agencyUpdatePage.getPageTitle()).to.eq('cvsApp.agency.home.createOrEditLabel');
    await agencyUpdatePage.cancel();
  });

  it('should create and save Agencies', async () => {
    const nbButtonsBeforeCreate = await agencyComponentsPage.countDeleteButtons();

    await agencyComponentsPage.clickOnCreateButton();

    await promise.all([
      agencyUpdatePage.setNameInput('name'),
      agencyUpdatePage.setLinkInput('link'),
      agencyUpdatePage.setDescriptionInput('description'),
      agencyUpdatePage.setLogopathInput('logopath'),
      agencyUpdatePage.setLicenseInput('license'),
      agencyUpdatePage.setLicenseIdInput('5'),
      agencyUpdatePage.setUriInput('uri'),
      agencyUpdatePage.setCanonicalUriInput('canonicalUri')
    ]);

    expect(await agencyUpdatePage.getNameInput()).to.eq('name', 'Expected Name value to be equals to name');
    expect(await agencyUpdatePage.getLinkInput()).to.eq('link', 'Expected Link value to be equals to link');
    expect(await agencyUpdatePage.getDescriptionInput()).to.eq('description', 'Expected Description value to be equals to description');
    expect(await agencyUpdatePage.getLogopathInput()).to.eq('logopath', 'Expected Logopath value to be equals to logopath');
    expect(await agencyUpdatePage.getLicenseInput()).to.eq('license', 'Expected License value to be equals to license');
    expect(await agencyUpdatePage.getLicenseIdInput()).to.eq('5', 'Expected licenseId value to be equals to 5');
    expect(await agencyUpdatePage.getUriInput()).to.eq('uri', 'Expected Uri value to be equals to uri');
    expect(await agencyUpdatePage.getCanonicalUriInput()).to.eq('canonicalUri', 'Expected CanonicalUri value to be equals to canonicalUri');

    await agencyUpdatePage.save();
    expect(await agencyUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await agencyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Agency', async () => {
    const nbButtonsBeforeDelete = await agencyComponentsPage.countDeleteButtons();
    await agencyComponentsPage.clickOnLastDeleteButton();

    agencyDeleteDialog = new AgencyDeleteDialog();
    expect(await agencyDeleteDialog.getDialogTitle()).to.eq('cvsApp.agency.delete.question');
    await agencyDeleteDialog.clickOnConfirmButton();

    expect(await agencyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
