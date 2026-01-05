import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../admin.service';
import { ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-panel.html',
  styleUrls: ['./admin-panel.scss']
})
export class AdminPanelComponent implements OnInit {

  users: any[] = [];
  loading = false;
  error = '';

  constructor(
    private adminService: AdminService,
     private auth: AuthService,
     private cdr: ChangeDetectorRef
    ) {}

  ngOnInit(): void {
    this.loadUsers();
  }
isSelf(user: any): boolean {
  return this.auth.getUserId() === user.id;
}

  loadUsers(): void {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();
    this.adminService.getAllUsers().subscribe({
      next: res => {
        this.users = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.error = 'Failed to load users';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

toggleActive(user: any): void {
  if (this.isSelf(user)) {
    alert('You cannot deactivate your own account');
    return;
  }

  this.loading = true;
  this.adminService
    .updateUser(user.id, { active: !user.active })
    .subscribe(() => this.loadUsers());
}


  updateRole(user: any, role: string): void {
    if (this.isSelf(user) && role !== 'ADMIN') {
    alert('You cannot remove your own ADMIN role');
    return;
  }
  if (user.roles.includes('ADMIN') && role !== 'ADMIN') {
    const confirmChange = confirm(
      'Removing ADMIN role can lock access. Continue?'
    );
    if (!confirmChange) return;
  }

  this.adminService
    .updateUser(user.id, { roles: [role] })
    .subscribe(() => this.loadUsers());
}

}
