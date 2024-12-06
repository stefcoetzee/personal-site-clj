/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.clj"],
  theme: {
    extend: {
      fontFamily: {
        sans: [
          "Lato",
          '"Libre Franklin"',
          '"Open Sans"',
          "ui-sans-serif",
          "system-ui",
          "-apple-system",
          "BlinkMacSystemFont",
          '"Segoe UI"',
          "Roboto",
          '"Helvetica Neue"',
          "Arial",
          '"Noto Sans"',
          "sans-serif",
          '"Apple Color Emoji"',
          '"Segoe UI Emoji"',
          '"Segoe UI Symbol"',
          '"Noto Color Emoji"',
        ],
        serif: [
          '"EB Garamond"',
          "ui-serif",
          "Georgia",
          "Cambria",
          '"Times New Roman"',
          "Times",
          "serif",
        ],
        mono: [
          '"Roboto Mono"',
          "ui-monospace",
          "SFMono-Regular",
          "Menlo",
          "Monaco",
          "Consolas",
          '"Liberation Mono"',
          '"Courier New"',
          "monospace",
        ],
        heading: [
          '"Source Serif Pro"',
          "ui-serif",
          "Georgia",
          "Cambria",
          '"Times New Roman"',
          "Times",
          "serif",
        ],
      },
      typography: {
        xl: {
          css: {
            code: {
              fontSize: "0.875rem !important", // more aggressive reduction
            },
            pre: {
              fontSize: "0.875rem !important", // also reduce code block size
            },
          },
        },
      },
    },
  },
  plugins: [require("@tailwindcss/typography")],
};
