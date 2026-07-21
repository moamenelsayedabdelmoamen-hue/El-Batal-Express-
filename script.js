const sidebarToggleButtons = document.querySelectorAll('#sidebarToggle');
const sidebar = document.getElementById('sidebar');
const sidebarClose = document.getElementById('sidebarClose');
const editRestaurantButton = document.getElementById('editRestaurant');
const saveRestaurantButton = document.getElementById('saveRestaurant');
const restaurantFields = ['restaurantName', 'restaurantPhone', 'restaurantAddress'];

function toggleSidebar() {
  if (!sidebar) return;
  sidebar.classList.toggle('open');
}

sidebarToggleButtons.forEach((button) => {
  button.addEventListener('click', toggleSidebar);
});

if (sidebarClose) {
  sidebarClose.addEventListener('click', () => sidebar.classList.remove('open'));
}

if (editRestaurantButton && saveRestaurantButton) {
  editRestaurantButton.addEventListener('click', () => {
    restaurantFields.forEach((fieldId) => {
      const field = document.getElementById(fieldId);
      if (field) field.removeAttribute('readonly');
    });
    saveRestaurantButton.removeAttribute('disabled');
    editRestaurantButton.disabled = true;
  });

  saveRestaurantButton.addEventListener('click', () => {
    restaurantFields.forEach((fieldId) => {
      const field = document.getElementById(fieldId);
      if (field) field.setAttribute('readonly', '');
    });
    saveRestaurantButton.disabled = true;
    editRestaurantButton.disabled = false;
    alert('تم حفظ بيانات المطعم مؤقتاً.');
  });
}

const dateInput = document.getElementById('historyDate');
const historyResults = document.getElementById('historyResults');

if (dateInput && historyResults) {
  const ordersByDate = {
    '2026-07-15': [
      {
        client: 'نورا سالم',
        phone: '01022223333',
        address: 'مدينة نصر، القاهرة',
        captain: 'علي إبراهيم',
        captainPhone: '01133445566',
      },
      {
        client: 'محمود عبد الله',
        phone: '01122334455',
        address: 'أكتوبر، الجيزة',
        captain: 'منى سمير',
        captainPhone: '01177889900',
      },
    ],
    '2026-07-16': [
      {
        client: 'ليلى حسن',
        phone: '01098765432',
        address: 'المعادي، القاهرة',
        captain: 'رامي محمود',
        captainPhone: '01155667744',
      },
    ],
  };

  dateInput.addEventListener('change', () => {
    const dateValue = dateInput.value;
    const orders = ordersByDate[dateValue] || [];

    if (!orders.length) {
      historyResults.innerHTML = '<p class="empty-state">لا توجد طلبات لهذا التاريخ.</p>';
      return;
    }

    const rows = orders.map((order) => `
      <div class="order-card">
        <div class="order-info">
          <p><strong>العميل:</strong> ${order.client}</p>
          <p><strong>الهاتف:</strong> ${order.phone}</p>
          <p><strong>العنوان:</strong> ${order.address}</p>
          <p><strong>المندوب:</strong> ${order.captain}</p>
          <p><strong>هاتف المندوب:</strong> ${order.captainPhone}</p>
        </div>
      </div>
    `);

    historyResults.innerHTML = rows.join('');
  });
}
