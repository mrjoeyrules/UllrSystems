// roleCheck.js
document.addEventListener("DOMContentLoaded", function() {

  // 1) Identify the admin-only links by their IDs
  const manageAccountsLink = document.getElementById("manageAccountsLink");
  const manageFridgesLink  = document.getElementById("manageFridgesLink");
  const reportsLink        = document.getElementById("reportsLink");
  const createAccountLink = document.getElementById("createAccountLink");

  // 2) If these elements exist, attach the event handlers
  if (manageAccountsLink) {
    manageAccountsLink.addEventListener("click", function(event) {
      event.preventDefault();  // Stop default immediate navigation
      checkRoleAndNavigate("ManageAccounts.html");
    });
  }

  if (manageFridgesLink) {
    manageFridgesLink.addEventListener("click", function(event) {
      event.preventDefault();
      checkRoleAndNavigate("ManageFridges.html");
    });
  }

  if (reportsLink) {
    reportsLink.addEventListener("click", function(event) {
      event.preventDefault();
      checkRoleAndNavigate("Reports.html");
    });
  }
  
  if (createAccountLink) {
    createAccountLink.addEventListener("click", function(event) {
      event.preventDefault(); // Stop immediate navigation
      checkRoleAndNavigate("CreateAccount.html");
    });
  }

});

// 3) This function fetches the current user’s role from the server.
//    If role === 3, navigate to the target; else alert and do nothing.
async function checkRoleAndNavigate(targetUrl) {
  try {
    const response = await fetch("/UllrSystems/getUserRole"); 
    if (!response.ok) {
      throw new Error("Error fetching user role from server.");
    }
    const data = await response.json();

    // If user is admin (role=3), allow navigation
    if (data.role === 3) {
      window.location.href = targetUrl;
    } else {
      alert("Only an Admin can access this page.");
    }
  } catch (err) {
    console.error(err);
    alert("Failed to check user role. Please try again.");
  }
}



