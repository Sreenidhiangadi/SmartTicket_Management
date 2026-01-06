import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AdminService } from '../admin.service';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-home.html',
  styleUrls: ['./admin-home.scss']
})
export class AdminHomeComponent implements OnInit {

  totalUsers = 0;
  activeUsers = 0;

  constructor(
    private adminService: AdminService,
    private router: Router,
  private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.adminService.getAllUsers().subscribe(users => {
      this.totalUsers = users.length;
      this.activeUsers = users.filter(u => u.active).length;
      this.cdr.detectChanges();
    });
  }


  goToUsers(): void {
    this.router.navigate(['/admin/users']);
  }
}
