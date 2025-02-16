window.addEventListener('DOMContentLoaded', event => {
    // Simple-DataTables
    // https://github.com/fiduswriter/Simple-DataTables/wiki

    const datatablesSimple = document.getElementById('datatablesSimple');
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple);
    }
});


//
// document.addEventListener('DOMContentLoaded', function() {
//   // Initialize the DataTable
//   const dataTable = new simpleDatatables.DataTable("#datatablesSimple");
//
//   // Custom search function
//   dataTable.on('datatable.search', function(query) {
//     const rows = dataTable.body.querySelectorAll('tr');
//     rows.forEach(row => {
//       const cells = row.querySelectorAll('td');
//       const rowData = Array.from(cells).map(cell => cell.textContent.toLowerCase()).join(' ');
//       const searchTerm = query.toLowerCase();
//
//       if (rowData.includes(searchTerm)) {
//         row.style.display = '';
//       } else {
//         row.style.display = 'none';
//       }
//     });
//   });
// });
