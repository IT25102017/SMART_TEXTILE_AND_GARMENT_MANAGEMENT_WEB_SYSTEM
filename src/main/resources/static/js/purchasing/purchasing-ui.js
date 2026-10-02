document.addEventListener("DOMContentLoaded", () => {
// Client-side table search
document.querySelectorAll("[data-table-search]")
.forEach((input) => {
const tableId =
input.getAttribute("data-table-search");
const table =
document.getElementById(tableId);
if (!table) {
return;
}
input.addEventListener("input", () => {
const keyword =
input.value
.trim()
.toLowerCase();
table.querySelectorAll("tbody tr")
.forEach((row) => {
if (row.classList.contains(
"purchasing-empty-row")) {
return;
}
const rowText =
row.textContent
.toLowerCase();
row.style.display =
rowText.includes(keyword)
? ""
: "none";
});
});
});
// Purchase Order Item line-total preview
const quantityInput =
document.getElementById("quantity");
const unitPriceInput =
document.getElementById("unitPrice");
const lineTotalOutput =
document.getElementById("lineTotal");
const updateLineTotal = () => {
if (!quantityInput
|| !unitPriceInput
|| !lineTotalOutput) {
return;
}
const quantity =
parseFloat(quantityInput.value) || 0;
const unitPrice =
parseFloat(unitPriceInput.value) || 0;
const total =
quantity * unitPrice;
lineTotalOutput.textContent =
total.toFixed(2);
};
if (quantityInput && unitPriceInput) {
quantityInput.addEventListener(
"input",
updateLineTotal
);
unitPriceInput.addEventListener(
"input",
updateLineTotal
);
updateLineTotal();
}
});