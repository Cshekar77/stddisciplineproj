// ── Theme ────────────────────────────────────────────────────────────────────
const html = document.documentElement;
const themeBtn = document.getElementById('themeBtn');
let isDark = (localStorage.getItem('sds-theme') || 'dark') === 'dark';
function applyTheme() {
  html.setAttribute('data-theme', isDark ? 'dark' : 'light');
  themeBtn.textContent = isDark ? '\uD83C\uDF19' : '\u2600\uFE0F';
  localStorage.setItem('sds-theme', isDark ? 'dark' : 'light');
}
applyTheme();
themeBtn.addEventListener('click', function() { isDark = !isDark; applyTheme(); });

// ── Password toggle ──────────────────────────────────────────────────────────
function togglePassword() {
  const input = document.getElementById('passwordInput');
  const eye   = document.getElementById('pwEye');
  input.type  = input.type === 'password' ? 'text' : 'password';
  eye.textContent = input.type === 'password' ? '\uD83D\uDC41\uFE0F' : '\uD83D\uDE48';
}

// ── Ripple effect ────────────────────────────────────────────────────────────
function rippleEffect(e) {
  const btn  = e.currentTarget;
  const span = document.createElement('span');
  const d    = Math.max(btn.clientWidth, btn.clientHeight);
  const rect = btn.getBoundingClientRect();
  span.className = 'ripple';
  span.style.width  = d + 'px';
  span.style.height = d + 'px';
  span.style.left   = (e.clientX - rect.left  - d / 2) + 'px';
  span.style.top    = (e.clientY - rect.top   - d / 2) + 'px';
  btn.appendChild(span);
  setTimeout(function() { span.remove(); }, 600);
}

// ── Loading state on form submit ──────────────────────────────────────────────
document.getElementById('loginForm').addEventListener('submit', function() {
  const btn = document.getElementById('loginBtn');
  btn.disabled = true;
  btn.style.opacity = '0.8';
  btn.innerHTML = '<span style="display:inline-flex;align-items:center;gap:8px;">'
    + '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">'
    + '<path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83">'
    + '<animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="0.7s" repeatCount="indefinite"/>'
    + '</path></svg>'
    + 'Signing in...</span>';
});

// ── Role data ────────────────────────────────────────────────────────────────
var roleData = {
  admin: {
    hint:    'Sign in as <strong>Admin</strong> to manage teachers, students, incidents and the full system.',
    hintCls: '',
    btnCls:  'btn-admin',
    btnTxt:  'Sign In as Admin',
    tabCls:  'active-admin',
    f0icon: '\uD83D\uDCCA', f0lbl: 'Analytics',
    f1icon: '\uD83D\uDD12', f1lbl: 'Sanctions',
    f2icon: '\uD83D\uDD75', f2lbl: 'Reports',
    pal: ['#4f7fff','#a78bfa','#ff6b6b','#43d9a2','#f7c948']
  },
  teacher: {
    hint:    'Sign in as <strong>Teacher</strong> to manage students, file incidents and apply sanctions.',
    hintCls: 'hint-teacher',
    btnCls:  'btn-teacher',
    btnTxt:  'Sign In as Teacher',
    tabCls:  'active-teacher',
    f0icon: '\uD83D\uDCCB', f0lbl: 'Incidents',
    f1icon: '\uD83D\uDD12', f1lbl: 'Sanctions',
    f2icon: '\uD83D\uDCAC', f2lbl: 'Feedback',
    pal: ['#43d9a2','#4f7fff','#f7c948','#a78bfa','#ff6b6b']
  },
  student: {
    hint:    'Sign in as <strong>Student</strong> to view your discipline record and submit reports.',
    hintCls: 'hint-student',
    btnCls:  'btn-student',
    btnTxt:  'Sign In as Student',
    tabCls:  'active-student',
    f0icon: '\uD83D\uDCC4', f0lbl: 'My Record',
    f1icon: '\uD83D\uDD12', f1lbl: 'Sanctions',
    f2icon: '\uD83D\uDD75', f2lbl: 'Reports',
    pal: ['#f7c948','#ff6b6b','#43d9a2','#4f7fff','#a78bfa']
  }
};

var activePal = roleData.admin.pal;

function selectRole(role) {
  ['admin','teacher','student'].forEach(function(r) {
    document.getElementById('tab-' + r).className =
      'role-tab' + (r === role ? ' ' + roleData[r].tabCls : '');
  });
  var hint = document.getElementById('roleHint');
  hint.innerHTML = roleData[role].hint;
  hint.className = 'role-hint show ' + roleData[role].hintCls;
  var btn = document.getElementById('loginBtn');
  btn.className   = 'btn-login ' + roleData[role].btnCls;
  btn.textContent = roleData[role].btnTxt;
  document.getElementById('loginForm').className = 'role-' + role;
  var feats = document.querySelectorAll('.feature');
  var d = roleData[role];
  feats[0].innerHTML = '<span class="ficon">' + d.f0icon + '</span>' + d.f0lbl;
  feats[1].innerHTML = '<span class="ficon">' + d.f1icon + '</span>' + d.f1lbl;
  feats[2].innerHTML = '<span class="ficon">' + d.f2icon + '</span>' + d.f2lbl;
  activePal = roleData[role].pal;
  spawnBurst();
}

// ═══════════════════════════════════════════════════════════════════════════
// CANVAS ANIMATION ENGINE
// ═══════════════════════════════════════════════════════════════════════════
var canvas = document.getElementById('c');
var ctx    = canvas.getContext('2d');
var W = 0, H = 0;

function resize() { W = canvas.width = window.innerWidth; H = canvas.height = window.innerHeight; }
window.addEventListener('resize', resize);
resize();

function rnd(n)  { return Math.random() * n; }
function rnp(n)  { return rnd(n * 2) - n; }
function toHex(al) {
  return Math.round(Math.max(0, Math.min(1, al)) * 255).toString(16).padStart(2, '0');
}

// Orbiting circles
var orbs = [];
for (var i = 0; i < 20; i++) {
  orbs.push({ cx: rnd(1), cy: rnd(1), or: rnd(160)+50, a: rnd(Math.PI*2),
    sp: (rnd(0.006)+0.001) * (Math.random()>0.5?1:-1),
    r: rnd(16)+5, ci: Math.floor(rnd(5)), al: rnd(0.15)+0.06,
    ps: rnd(Math.PI*2), psp: rnd(0.025)+0.008 });
}

// Shooting stars
var stars = [];
function spawnStar() {
  stars.push({ x: rnd(W), y: rnd(H*0.5), vx: rnd(6)+3, vy: rnd(3)+1,
    life: 1, maxLife: rnd(60)+40, ci: Math.floor(rnd(5)), len: rnd(80)+40 });
}
for (var s = 0; s < 6; s++) spawnStar();

// Floating particles
var parts = [];
function spawnParticle(x, y, ci) {
  parts.push({ x: x!==undefined?x:rnd(W), y: y!==undefined?y:rnd(H),
    vx: rnp(1.2), vy: rnp(1.2)-0.5, r: rnd(3)+1, life: 1,
    decay: rnd(0.008)+0.003, ci: ci!==undefined?ci:Math.floor(rnd(5)), al: rnd(0.3)+0.15 });
}
for (var p = 0; p < 60; p++) spawnParticle();

// Burst
function spawnBurst() {
  for (var b = 0; b < 25; b++) {
    var angle = rnd(Math.PI*2), spd = rnd(4)+1;
    parts.push({ x: W*0.5, y: H*0.5, vx: Math.cos(angle)*spd, vy: Math.sin(angle)*spd,
      r: rnd(5)+2, life: 1, decay: rnd(0.015)+0.008, ci: Math.floor(rnd(5)), al: 0.6 });
  }
}

var mx = 0, my = 0;
window.addEventListener('mousemove', function(e) { mx = e.clientX; my = e.clientY; });

var t = 0;
function draw() {
  ctx.clearRect(0, 0, W, H);
  t += 0.012;
  var pal = activePal;

  // Orbiting circles
  for (var oi = 0; oi < orbs.length; oi++) {
    var o = orbs[oi]; o.a += o.sp;
    var ox = o.cx*W + Math.cos(o.a)*o.or;
    var oy = o.cy*H + Math.sin(o.a)*o.or;
    var pr = o.r * (1 + 0.15*Math.sin(t*o.psp*5+o.ps));
    var col = pal[o.ci % pal.length];
    ctx.beginPath(); ctx.arc(o.cx*W, o.cy*H, o.or, 0, Math.PI*2);
    ctx.strokeStyle = col+'12'; ctx.lineWidth = 1; ctx.stroke();
    var g1 = ctx.createRadialGradient(ox,oy,0,ox,oy,pr*3);
    g1.addColorStop(0,col+toHex(o.al)); g1.addColorStop(1,col+'00');
    ctx.beginPath(); ctx.arc(ox,oy,pr*3,0,Math.PI*2); ctx.fillStyle=g1; ctx.fill();
    ctx.beginPath(); ctx.arc(ox,oy,pr,0,Math.PI*2); ctx.fillStyle=col+toHex(o.al+0.1); ctx.fill();
  }

  // Shooting stars
  for (var si = stars.length-1; si >= 0; si--) {
    var st = stars[si]; st.x+=st.vx; st.y+=st.vy; st.life-=1/st.maxLife;
    if (st.life<=0||st.x>W+100) { stars.splice(si,1); spawnStar(); continue; }
    var sc = pal[st.ci%pal.length], sal = Math.min(st.life*2,1)*0.7;
    var tx = st.x-st.vx*(st.len/st.vx), ty = st.y-st.vy*(st.len/st.vx);
    var sg = ctx.createLinearGradient(st.x,st.y,tx,ty);
    sg.addColorStop(0,sc+toHex(sal)); sg.addColorStop(1,sc+'00');
    ctx.beginPath(); ctx.moveTo(st.x,st.y); ctx.lineTo(tx,ty);
    ctx.strokeStyle=sg; ctx.lineWidth=1.5; ctx.stroke();
    ctx.beginPath(); ctx.arc(st.x,st.y,2.5,0,Math.PI*2); ctx.fillStyle=sc+toHex(sal); ctx.fill();
  }

  // Connection lines
  for (var li = 0; li < parts.length; li++) {
    for (var lj = li+1; lj < parts.length; lj++) {
      var ldx=parts[li].x-parts[lj].x, ldy=parts[li].y-parts[lj].y;
      var ld=Math.sqrt(ldx*ldx+ldy*ldy);
      if (ld<110) {
        var lal=(0.5-ld/220)*Math.min(parts[li].life,parts[lj].life)*0.5;
        ctx.beginPath(); ctx.moveTo(parts[li].x,parts[li].y); ctx.lineTo(parts[lj].x,parts[lj].y);
        ctx.strokeStyle=pal[parts[li].ci%pal.length]+toHex(lal); ctx.lineWidth=0.7; ctx.stroke();
      }
    }
  }

  // Particles
  if (Math.random()<0.18) spawnParticle();
  for (var pi = parts.length-1; pi >= 0; pi--) {
    var pt=parts[pi];
    var pdx=pt.x-mx, pdy=pt.y-my, pds=Math.sqrt(pdx*pdx+pdy*pdy);
    if (pds<120&&pds>0) { var pf=0.4*(1-pds/120); pt.vx+=(pdx/pds)*pf; pt.vy+=(pdy/pds)*pf; }
    pt.vy-=0.002; pt.x+=pt.vx; pt.y+=pt.vy; pt.vx*=0.98; pt.vy*=0.98; pt.life-=pt.decay;
    if (pt.life<=0||pt.x<-20||pt.x>W+20||pt.y<-20||pt.y>H+20) { parts.splice(pi,1); continue; }
    var pc=pal[pt.ci%pal.length], pa2=pt.life*pt.al;
    var pg=ctx.createRadialGradient(pt.x,pt.y,0,pt.x,pt.y,pt.r*2.5);
    pg.addColorStop(0,pc+toHex(pa2)); pg.addColorStop(1,pc+'00');
    ctx.beginPath(); ctx.arc(pt.x,pt.y,pt.r*2.5,0,Math.PI*2); ctx.fillStyle=pg; ctx.fill();
    ctx.beginPath(); ctx.arc(pt.x,pt.y,pt.r,0,Math.PI*2); ctx.fillStyle=pc+toHex(pa2+0.1); ctx.fill();
  }

  requestAnimationFrame(draw);
}
draw();