document.addEventListener("DOMContentLoaded", function() {
  // ... existing references for manageAccountsLink, etc.

  // For the logout link in the sidebar:
  const logoutLink = document.getElementById("logoutLink");
  if (logoutLink) {
    logoutLink.addEventListener("click", function(event) {
      event.preventDefault();
      // Simply navigate back to index.html
      window.location.href = "index.html";
    });
  }

  // OR if you have a logout button in the header:
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", function() {
      window.location.href = "index.html";
    });
  }
});



