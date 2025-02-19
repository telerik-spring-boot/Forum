/// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily =
    '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = "#292b2c";

// Function to get the last 12 months
function getLast12Months() {
    const months = [];
    const today = new Date();

    for (let i = 11; i >= 0; i--) {
        const pastDate = new Date();
        pastDate.setMonth(today.getMonth() - i);

        // Format month as "MMM YYYY" (e.g., "Feb 2024")
        const formattedMonth = pastDate.toLocaleDateString("en-US", {
            month: "short",
            year: "numeric"
        });

        months.push(formattedMonth);
    }
    return months;
}


// Generate random data for demonstration
function generateRandomData() {
    return Array.from({length: 12}, () => Math.floor(Math.random() * 40000));
}

// Function to extract lastLogin data from table
function extractLastLoginData() {
    const lastLoginData = [];
    const tableRows = document.querySelectorAll("table tr");

    // Loop through each row and extract the lastLogin from the 5th td (index 4)
    tableRows.forEach((row, index) => {
        if (index >= 2) { // Skip the first two rows (headers)
            const lastLoginCell = row.children[4]; // 5th td, index 4
            if (lastLoginCell) {
                const lastLogin = lastLoginCell.innerText.trim();
                lastLoginData.push(lastLogin);
            }
        }
    });

    return lastLoginData;
}


// Function to convert lastLogin string to 'MMM YYYY' format (e.g., "Feb 2024")
function formatDateToYearMonth(dateStr) {
    // Split the date string to get "YYYY-MM-DD"
    const dateParts = dateStr.split(" ")[0].split("-");
    const year = dateParts[0];
    const month = parseInt(dateParts[1]) - 1; // JavaScript months are 0-indexed, so subtract 1

    return new Date(year, month).toLocaleString('en-US', {
        month: 'short',
        year: 'numeric'
    });
}

// Function to count users active for each month in the last 12 months
function countLoginsByMonth(lastLoginData) {
    const counts = {};
    const last12Months = getLast12Months();

    // Count logins for each month
    lastLoginData.forEach(dateStr => {
        const monthYear = formatDateToYearMonth(dateStr);
        if (last12Months.includes(monthYear)) {
            counts[monthYear] = (counts[monthYear] || 0) + 1;
        }
    });
    // Fill in missing months with 0 logins
    last12Months.forEach(month => {
        if (!counts[month]) {
            counts[month] = 0;
        }
    });

    // Return counts in the same order as the last12Months array
    return last12Months.map(month => counts[month]);
}

const postDatesArray = postDates.substring(1, postDates.length - 1).split(",").map(date => date.trim());


// Function to count post created for each month in the last 12 months
function countCreatedPosts(postDatesArray) {
    const counts = {};
    const last12Months = getLast12Months();

    // Count logins for each month
    postDatesArray.forEach(dateStr => {
        const monthYear = formatDateToYearMonth(dateStr);
        if (last12Months.includes(monthYear)) {
            counts[monthYear] = (counts[monthYear] || 0) + 1;
        }
    });
    // Fill in missing months with 0 logins
    last12Months.forEach(month => {
        if (!counts[month]) {
            counts[month] = 0;
        }
    });
    // Return counts in the same order as the last12Months array
    return last12Months.map(month => counts[month]);
}

// Area Chart Example
function createLineChart(elementId, label, data, backgroundColor, borderColor) {
    var ctx = document.getElementById(elementId);
    return new Chart(ctx, {
        type: "line",
        data: {
            labels: getLast12Months(),
            datasets: [
                {
                    label: label,
                    lineTension: 0.3,
                    backgroundColor: backgroundColor,
                    borderColor: borderColor,
                    pointRadius: 5,
                    pointBackgroundColor: borderColor,
                    pointBorderColor: "rgba(255,255,255,0.8)",
                    pointHoverRadius: 5,
                    pointHoverBackgroundColor: borderColor,
                    pointHitRadius: 50,
                    pointBorderWidth: 2,
                    data: data,
                },
            ],
        },
        options: {
            scales: {
                xAxes: [
                    {
                        time: {
                            unit: "date",
                        },
                        gridLines: {
                            display: false,
                        },
                        ticks: {
                            maxTicksLimit: 7,
                        },
                    },
                ],
                yAxes: [
                    {
                        ticks: {
                            min: 0,
                            max: Math.round(Math.max(...data) * 120 / 100),
                            stepSize: Math.round(Math.max(...data) * 120 / 100 / 10),
                            maxTicksLimit: 10,
                        },
                        gridLines: {
                            color: "rgba(0, 0, 0, .125)",
                        },
                    },
                ],
            },
            legend: {
                display: false,
            },
        },
    });
}

createLineChart("myAreaChart1", "Logged In Users", countLoginsByMonth(extractLastLoginData()), "rgba(2,117,216,0.2)", "rgba(2,117,216,1)");
createLineChart("myAreaChart2", "Created Posts", countCreatedPosts(postDatesArray), "rgba(255,99,132,0.2)", "rgba(255,99,132,1)");
