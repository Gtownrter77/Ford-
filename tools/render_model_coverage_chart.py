from pathlib import Path
import re
import matplotlib.pyplot as plt

inventory = Path('/tmp/sport_trac_model_inventory.md').read_text().splitlines()
counts = []
for line in inventory:
    match = re.fullmatch(r'([A-Z_]+)=(\d+)', line)
    if match:
        label = match.group(1).replace('_', ' ').replace('DRIVETRAIN ', 'DRIVETRAIN / ')
        counts.append((label.title(), int(match.group(2))))

counts.sort(key=lambda entry: (-entry[1], entry[0]))
labels = [entry[0] for entry in counts]
values = [entry[1] for entry in counts]
colors = ['#38BDF8' if label == 'Air Conditioning' else '#10B981' for label in labels]

plt.style.use('dark_background')
fig, ax = plt.subplots(figsize=(13, 8), facecolor='#0F172A')
ax.set_facecolor('#0F172A')
bars = ax.barh(labels, values, color=colors, edgecolor='#94A3B8', linewidth=0.6)
ax.invert_yaxis()
ax.set_xlim(0, max(values) + 2)
ax.set_xlabel('Modeled component records in current source', color='#E2E8F0', labelpad=10)
ax.set_title('2004 Ford Explorer Sport Trac 4.0L 4WD\nCurrent 3D Model Coverage (Source-Verified)', color='white', fontsize=18, fontweight='bold', pad=18)
ax.text(0, -1.1, 'This is a coverage inventory—not a claim that every possible repair, part, or Mentor conversation is complete.', color='#CBD5E1', fontsize=10)
ax.tick_params(colors='#E2E8F0')
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
ax.spines['left'].set_color('#475569')
ax.spines['bottom'].set_color('#475569')
for bar, value in zip(bars, values):
    ax.text(value + 0.15, bar.get_y() + bar.get_height() / 2, str(value), va='center', color='white', fontweight='bold')
fig.text(0.125, 0.02, 'Includes engine, cooling, A/C, brakes/chassis, electrical, drivetrain, transmission, intake, interior, lighting/body, and roof/rear-glass systems.', color='#94A3B8', fontsize=9)
plt.tight_layout(rect=(0, 0.06, 1, 1))
output = Path('/home/ubuntu/deliverables/ford-sport-trac-current-model-coverage.png')
output.parent.mkdir(parents=True, exist_ok=True)
fig.savefig(output, dpi=180, facecolor=fig.get_facecolor(), bbox_inches='tight')
print(output)
