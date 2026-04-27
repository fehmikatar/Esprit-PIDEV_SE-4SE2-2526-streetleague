# 🏆 Team Details View - Frontend Implementation Guide

## Overview
This guide explains how to implement the Team Details view with member display in your Angular application.

---

## 📡 Backend API Endpoint

### Get Team Details with Members
**Endpoint:** `GET /api/teams/{id}/details`

**Parameters:**
- `id` (path): Team ID (e.g., 1)

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Football Stars",
  "sport": "Football",
  "level": "Professional",
  "description": "Elite football team",
  "logo": "https://example.com/logo.png",
  "city": "Tunis",
  "status": "ACTIVE",
  "createdAt": "2026-04-05T10:30:00",
  "members": [
    {
      "userId": 5,
      "firstName": "Ahmed",
      "lastName": "Ben Ali",
      "email": "ahmed@example.com",
      "phone": "+216 12345678",
      "profileImageUrl": "https://example.com/user-img.png",
      "role": "RESPONSIBLE",
      "position": "Goalkeeper",
      "skillLevel": 9,
      "rating": 8.5
    },
    {
      "userId": 6,
      "firstName": "Fatima",
      "lastName": "Mekki",
      "email": "fatima@example.com",
      "phone": "+216 98765432",
      "profileImageUrl": "https://example.com/fatima-img.png",
      "role": "MEMBER",
      "position": "Forward",
      "skillLevel": 8,
      "rating": 8.2
    }
  ]
}
```

---

## 🎯 Frontend Implementation

### 1. Service Setup

Create/Update your `team.service.ts`:

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TeamMember {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  profileImageUrl: string;
  role: 'MEMBER' | 'RESPONSIBLE';
  position?: string;      // Only for Players
  skillLevel?: number;    // Only for Players
  rating?: number;        // Only for Players
}

export interface TeamDetails {
  id: number;
  name: string;
  sport: string;
  level: string;
  description: string;
  logo: string;
  city: string;
  status: string;
  createdAt: Date;
  members: TeamMember[];
}

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private apiUrl = 'http://localhost:8085/api/teams';

  constructor(private http: HttpClient) {}

  /**
   * Get team details with all members
   */
  getTeamDetails(teamId: number): Observable<TeamDetails> {
    return this.http.get<TeamDetails>(`${this.apiUrl}/${teamId}/details`);
  }

  /**
   * Get team by ID (basic info only)
   */
  getTeam(teamId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${teamId}`);
  }
}
```

### 2. Component Setup

Create `team-details.component.ts`:

```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TeamService, TeamDetails, TeamMember } from '../services/team.service';

@Component({
  selector: 'app-team-details',
  templateUrl: './team-details.component.html',
  styleUrls: ['./team-details.component.css']
})
export class TeamDetailsComponent implements OnInit {
  
  teamDetails: TeamDetails | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.loadTeamDetails();
  }

  loadTeamDetails(): void {
    const teamId = Number(this.route.snapshot.paramMap.get('id'));
    
    this.teamService.getTeamDetails(teamId).subscribe({
      next: (data) => {
        this.teamDetails = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading team details:', err);
        this.error = 'Failed to load team details. Please try again.';
        this.loading = false;
      }
    });
  }

  /**
   * Get full name of a member
   */
  getFullName(member: TeamMember): string {
    return `${member.firstName} ${member.lastName}`.trim();
  }

  /**
   * Check if member is responsible
   */
  isResponsible(member: TeamMember): boolean {
    return member.role === 'RESPONSIBLE';
  }

  /**
   * Get role badge class
   */
  getRoleBadgeClass(role: string): string {
    return role === 'RESPONSIBLE' ? 'badge-primary' : 'badge-secondary';
  }

  /**
   * Format skill level as stars (1-10)
   */
  getSkillStars(skillLevel: number | undefined): string[] {
    if (!skillLevel) return [];
    return Array(skillLevel).fill('★');
  }
}
```

### 3. Template Setup

Create `team-details.component.html`:

```html
<div class="team-details-container">
  
  <!-- Loading State -->
  <div *ngIf="loading" class="spinner">
    <p>Loading team details...</p>
  </div>

  <!-- Error State -->
  <div *ngIf="error && !loading" class="alert alert-danger">
    <p>{{ error }}</p>
    <button (click)="loadTeamDetails()" class="btn btn-primary">Retry</button>
  </div>

  <!-- Team Details View -->
  <div *ngIf="teamDetails && !loading" class="team-details">
    
    <!-- Header Section -->
    <div class="team-header">
      <div class="team-logo">
        <img [src]="teamDetails.logo" [alt]="teamDetails.name" />
      </div>
      <div class="team-info">
        <h1>{{ teamDetails.name }}</h1>
        <div class="team-meta">
          <span class="badge" [ngClass]="'badge-' + (teamDetails.status === 'ACTIVE' ? 'success' : 'warning')">
            {{ teamDetails.status }}
          </span>
          <span class="team-sport">⚽ {{ teamDetails.sport }}</span>
          <span class="team-level">📊 {{ teamDetails.level }}</span>
          <span class="team-city">📍 {{ teamDetails.city }}</span>
        </div>
      </div>
    </div>

    <!-- Team Description -->
    <div class="team-description" *ngIf="teamDetails.description">
      <p>{{ teamDetails.description }}</p>
    </div>

    <!-- Team Members Section -->
    <div class="team-members-section">
      <h2>👥 Team Members ({{ teamDetails.members.length }})</h2>
      
      <!-- Members Grid -->
      <div class="members-grid">
        
        <!-- Member Card (Responsive) -->
        <div *ngFor="let member of teamDetails.members" class="member-card">
          
          <!-- Member Profile Image -->
          <div class="member-avatar">
            <img 
              [src]="member.profileImageUrl" 
              [alt]="getFullName(member)"
              class="avatar-img"
            />
            <span [ngClass]="getRoleBadgeClass(member.role)" class="role-badge">
              {{ member.role }}
            </span>
          </div>

          <!-- Member Info -->
          <div class="member-info">
            <h3>{{ getFullName(member) }}</h3>
            
            <!-- Player-specific Info -->
            <div *ngIf="member.position" class="player-details">
              <p><strong>Position:</strong> {{ member.position }}</p>
              <p><strong>Skill Level:</strong> <span class="skill-stars">{{ getSkillStars(member.skillLevel).join('') }}</span> {{ member.skillLevel }}/10</p>
              <p><strong>Rating:</strong> ⭐ {{ member.rating || 'N/A' }}</p>
            </div>

            <!-- Contact Info -->
            <div class="contact-info">
              <p>
                <strong>Email:</strong> 
                <a [href]="'mailto:' + member.email">{{ member.email }}</a>
              </p>
              <p *ngIf="member.phone">
                <strong>Phone:</strong> 
                <a [href]="'tel:' + member.phone">{{ member.phone }}</a>
              </p>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="member-actions">
            <button class="btn btn-sm btn-outline-primary" title="Send Message">
              💬 Message
            </button>
            <button class="btn btn-sm btn-outline-danger" title="Remove Member">
              🗑️ Remove
            </button>
          </div>
        </div>

      </div>

      <!-- No Members State -->
      <div *ngIf="teamDetails.members.length === 0" class="empty-state">
        <p>No members in this team yet.</p>
        <button class="btn btn-primary">+ Add Member</button>
      </div>
    </div>

    <!-- Add Member Section (if current user is responsible) -->
    <div class="add-member-section">
      <button class="btn btn-primary btn-lg">
        + Add New Member
      </button>
    </div>

  </div>
  
</div>
```

### 4. Styling

Create `team-details.component.css`:

```css
/* Container */
.team-details-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

/* Header Section */
.team-header {
  display: flex;
  align-items: center;
  gap: 30px;
  background: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.team-logo {
  flex-shrink: 0;
}

.team-logo img {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
  border: 3px solid #007bff;
}

.team-info h1 {
  margin: 0 0 15px 0;
  font-size: 2.5rem;
  color: #212529;
}

.team-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.team-meta span {
  font-size: 0.95rem;
  padding: 6px 12px;
  background: #e9ecef;
  border-radius: 20px;
}

/* Description */
.team-description {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  border-left: 4px solid #007bff;
}

.team-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

/* Members Section */
.team-members-section {
  margin-bottom: 40px;
}

.team-members-section h2 {
  font-size: 1.8rem;
  margin-bottom: 20px;
  color: #212529;
}

/* Members Grid */
.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

/* Member Card */
.member-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
}

.member-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

/* Member Avatar */
.member-avatar {
  position: relative;
  margin-bottom: 15px;
}

.avatar-img {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  object-fit: cover;
}

.role-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
  color: white;
}

.badge-primary {
  background: #007bff;
}

.badge-secondary {
  background: #6c757d;
}

/* Member Info */
.member-info {
  flex-grow: 1;
}

.member-info h3 {
  margin: 0 0 10px 0;
  font-size: 1.3rem;
  color: #212529;
}

.player-details {
  margin: 15px 0;
  padding: 15px;
  background: #f0f4ff;
  border-radius: 6px;
  font-size: 0.9rem;
}

.player-details p {
  margin: 5px 0;
}

.skill-stars {
  color: #ffc107;
  font-size: 1.1rem;
  margin-right: 5px;
}

.contact-info {
  margin: 15px 0;
  font-size: 0.85rem;
}

.contact-info p {
  margin: 5px 0;
}

.contact-info a {
  color: #007bff;
  text-decoration: none;
}

.contact-info a:hover {
  text-decoration: underline;
}

/* Member Actions */
.member-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e9ecef;
}

.member-actions button {
  flex: 1;
  font-size: 0.85rem;
}

/* Empty State */
.empty-state {
  background: white;
  padding: 40px;
  border-radius: 8px;
  text-align: center;
  color: #999;
}

.empty-state p {
  margin-bottom: 20px;
  font-size: 1.1rem;
}

/* Add Member Section */
.add-member-section {
  text-align: center;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.add-member-section .btn-lg {
  padding: 12px 30px;
  font-size: 1.1rem;
}

/* Loading & Error States */
.spinner {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.alert {
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.alert-danger {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

/* Responsive Design */
@media (max-width: 768px) {
  .team-header {
    flex-direction: column;
    text-align: center;
  }

  .team-logo img {
    width: 120px;
    height: 120px;
  }

  .team-info h1 {
    font-size: 1.8rem;
  }

  .members-grid {
    grid-template-columns: 1fr;
  }

  .member-card {
    padding: 15px;
  }
}
```

### 5. Routing Setup

Update your `app-routing.module.ts`:

```typescript
const routes: Routes = [
  // ... other routes
  {
    path: 'teams/:id/details',
    component: TeamDetailsComponent
  },
  // ... more routes
];
```

---

## 📊 Data Structure Reference

### TeamDetails Object
| Field | Type | Description |
|-------|------|-------------|
| id | number | Team unique identifier |
| name | string | Team name |
| sport | string | Sport type (Football, Basketball, etc.) |
| level | string | Competition level (Amateur, Professional, etc.) |
| description | string | Team description |
| logo | string | Team logo URL |
| city | string | City where team is based |
| status | string | Team status (ACTIVE, INACTIVE, etc.) |
| createdAt | Date | Team creation date |
| members | TeamMember[] | Array of team members |

### TeamMember Object
| Field | Type | Description |
|-------|------|-------------|
| userId | number | User unique identifier |
| firstName | string | Member first name |
| lastName | string | Member last name |
| email | string | Member email |
| phone | string | Member phone number |
| profileImageUrl | string | Member profile image URL |
| role | string | Role in team (MEMBER, RESPONSIBLE) |
| position | string? | Player position (optional, if Player) |
| skillLevel | number? | Player skill level 1-10 (optional, if Player) |
| rating | number? | Player average rating (optional, if Player) |

---

## 🎮 Usage Example

### Navigate to Team Details
```typescript
// In your list view component or navigation
this.router.navigate(['/teams', teamId, 'details']);

// Or with query parameters for filtering
this.router.navigate(['/teams', teamId, 'details'], {
  queryParams: { tab: 'members' }
});
```

### Get Team Details in Component
```typescript
// In any component
this.teamService.getTeamDetails(teamId).subscribe(data => {
  console.log('Team members:', data.members);
});
```

---

## ✅ Testing Checklist

- [ ] Can retrieve team details with members list
- [ ] Members display with correct information
- [ ] Player-specific fields (position, skill level) show only for players
- [ ] Role badges display correctly (RESPONSIBLE vs MEMBER)
- [ ] Images load correctly
- [ ] Responsive design works on mobile
- [ ] Error handling displays properly
- [ ] No console errors

---

## 🔧 Troubleshooting

### Members list is empty
- Check if team has members in database
- Use MySQL to verify: `SELECT * FROM team_member WHERE team_id = ?`

### Images not loading
- Verify image URLs are valid and accessible
- Check CORS settings in backend if using external images

### 401 Unauthorized
- Ensure JWT token is included in Authorization header
- Token may have expired, user needs to log in again

### 404 Not Found
- Verify team ID exists
- Check URL pattern `/api/teams/{id}/details`

---

## 📝 Next Steps

1. Implement "Add Member" functionality (POST endpoint needed)
2. Implement "Remove Member" functionality (DELETE endpoint needed)
3. Add member search/filter capabilities
4. Add member role management

Contact backend team for these additional endpoints!
