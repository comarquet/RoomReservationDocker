<template>
  <div>
    <MainPage
      v-if="currentPage === 'main'"
      @switchToSignUp="showSignUpPage"
      @login="handleLogin"
    />
    <SignUpPage
      v-if="currentPage === 'signup'"
      @switchToMain="showMainPage"
      @createAccount="handleCreateAccount"
    />
    <AccountPage
      v-if="currentPage === 'account'"
      :user="loggedInUser"
      @logout="handleLogout"
    />
  </div>
</template>

<script>
import MainPage from './MainPage.vue';
import SignUpPage from './SignUpPage.vue';
import AccountPage from './AccountPage.vue';
import api from './api';

export default {
  data() {
    return {
      currentPage: 'main',
      loggedInUser: null,
    };
  },
  components: {
    MainPage,
    SignUpPage,
    AccountPage,
  },
  methods: {
    showSignUpPage() {
      this.currentPage = 'signup';
    },
    showMainPage() {
      this.currentPage = 'main';
    },
    async handleCreateAccount(accountDetails) {
      try {
        await api.createUser(accountDetails);
        alert('Account created successfully! You can now log in.');
        this.showMainPage();
      } catch (error) {
        alert(error.message);
      }
    },
    async handleLogin(email, password) {
      try {
        const user = await api.login(email, password);
        this.loggedInUser = user;
        this.currentPage = 'account';
      } catch (error) {
        alert('Invalid email or password.');
      }
    },
    handleLogout() {
      this.loggedInUser = null;
      this.showMainPage();
    },
  },
};
</script>