// Preserve spaces between words; remove leading whitespace while typing/pasting.
document
  .querySelectorAll(
    '#filter-form input[type="text"], #UserForm input[type="text"], #UserForm textarea',
  )
  .forEach((field) => {
    field.addEventListener("input", () => {
      field.dataset.whitespaceOnly = String(
        field.value !== "" && field.value.trim() === "",
      );
      field.value = field.value.trimStart();
    });
    field.addEventListener("blur", () => {
      field.value = field.value.trim();
    });
  });
document.querySelectorAll("#filter-form, #UserForm").forEach((form) => {
  form.addEventListener(
    "submit",
    () => {
      form.querySelectorAll('input[type="text"], textarea').forEach((field) => {
        if (field.value !== "" && field.value.trim() === "")
          field.dataset.whitespaceOnly = "true";
        field.value = field.value.trim();
      });
    },
    true,
  );
  form.addEventListener("reset", () => {
    form.querySelectorAll("[data-whitespace-only]").forEach((field) => {
      delete field.dataset.whitespaceOnly;
    });
  });
});
