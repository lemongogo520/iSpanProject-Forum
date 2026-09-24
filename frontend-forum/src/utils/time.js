const rtf = new Intl.RelativeTimeFormat("zh-TW", { numeric: "auto" });

const UNITS = [
  ["year", 31536000],
  ["month", 2592000],
  ["week", 604800],
  ["day", 86400],
  ["hour", 3600],
  ["minute", 60],
];

/** 相對時間：剛剛 / 5 分鐘前 / 昨天 / 上週 */
export function timeAgo(iso) {
  if (!iso) return "";
  const diffSec = (new Date(iso).getTime() - Date.now()) / 1000;
  for (const [unit, sec] of UNITS) {
    if (Math.abs(diffSec) >= sec) return rtf.format(Math.round(diffSec / sec), unit);
  }
  return "剛剛";
}

/** 時分：14:30 */
export function timeOf(iso) {
  if (!iso) return "";
  return new Date(iso).toLocaleTimeString("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  });
}

/** 日期分隔線：今天 / 昨天 / 9月12日 / 2025年12月3日 */
export function dateLabel(iso) {
  if (!iso) return "";
  const d = new Date(iso);
  const today = new Date();
  const dayDiff = Math.round(
    (new Date(today.getFullYear(), today.getMonth(), today.getDate()) -
      new Date(d.getFullYear(), d.getMonth(), d.getDate())) / 86400000
  );

  if (dayDiff === 0) return "今天";
  if (dayDiff === 1) return "昨天";

  return d.toLocaleDateString("zh-TW", {
    year: d.getFullYear() === today.getFullYear() ? undefined : "numeric",
    month: "long",
    day: "numeric",
  });
}

/** 同一天判斷，用於決定要不要插入日期分隔線 */
export function isSameDay(a, b) {
  if (!a || !b) return false;
  const d1 = new Date(a);
  const d2 = new Date(b);
  return d1.toDateString() === d2.toDateString();
}