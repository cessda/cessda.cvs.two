import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { UserAgencyComponentsPage, UserAgencyDeleteDialog, UserAgencyUpdatePage } from './user-agency.page-object';

const expect = chai.expect;

describe('UserAgency e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let userAgencyComponentsPage: UserAgencyComponentsPage;
  let userAgencyUpdatePage: UserAgencyUpdatePage;
  let userAgencyDeleteDialog: UserAgencyDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load UserAgencies', async () => {
    await navBarPage.goToEntity('user-agency');
    userAgencyComponentsPage = new UserAgencyComponentsPage();
    await browser.wait(ec.visibilityOf(userAgencyComponentsPage.title), 5000);
    expect(await userAgencyComponentsPage.getTitle()).to.eq('cvsApp.userAgency.home.title');
    await browser.wait(ec.or(ec.visibilityOf(userAgencyComponentsPage.entities), ec.visibilityOf(userAgencyComponentsPage.noResult)), 1000);
  });

  it('should load create UserAgency page', async () => {
    await userAgencyComponentsPage.clickOnCreateButton();
    userAgencyUpdatePage = new UserAgencyUpdatePage();
    expect(await userAgencyUpdatePage.getPageTitle()).to.eq('cvsApp.userAgency.home.createOrEditLabel');
    await userAgencyUpdatePage.cancel();
  });

  it('should create and save UserAgencies', async () => {
    const nbButtonsBeforeCreate = await userAgencyComponentsPage.countDeleteButtons();

    await userAgencyComponentsPage.clickOnCreateButton();

    await promise.all([
      userAgencyUpdatePage.agencyRoleSelectLastOption(),
      userAgencyUpdatePage.languageSelectLastOption(),
      userAgencyUpdatePage.agencySelectLastOption()
    ]);

    await userAgencyUpdatePage.save();
    expect(await userAgencyUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await userAgencyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last UserAgency', async () => {
    const nbButtonsBeforeDelete = await userAgencyComponentsPage.countDeleteButtons();
    await userAgencyComponentsPage.clickOnLastDeleteButton();

    userAgencyDeleteDialog = new UserAgencyDeleteDialog();
    expect(await userAgencyDeleteDialog.getDialogTitle()).to.eq('cvsApp.userAgency.delete.question');
    await userAgencyDeleteDialog.clickOnConfirmButton();

    expect(await userAgencyComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
