import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';
import { RoleGuard } from './core/auth/role.guard';
import { HomeComponent } from './features/home/home';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { AdminPanelComponent } from './features/admin/admin-panel/admin-panel';
import { AdminLayoutComponent } from './features/admin/admin-layout/admin-layout';
import { AdminHomeComponent } from './features/admin/admin-home/admin-home';
import { AgentQueueComponent } from './features/agent/agent-queue/agent-queue';
import { TicketDetailsComponent } from './features/tickets/ticket-details/ticket-details';
import { MyTicketsComponent } from './features/tickets/my-tickets/my-tickets';
import { CreateTicketComponent } from './features/tickets/create-ticket/create-ticket';
import { ManagerTicketListComponent } from './features/manager/manager-ticket-list/manager-ticket-list';
import { SlaBreachesComponent } from './features/admin/sla/sla-breaches/sla-breaches';
import { ManagerEscalationsComponent } from './features/escalation/manager-escalations/manager-escalations';
import { AdminEscalationsComponent } from './features/escalation/admin-escalation/admin-escalation';
import { ManagerDashboardComponent } from './features/manager/manager-dashboard/manager-dashboard';
import { ReportsComponent } from './features/reports/reports/reports';
import { ProfileComponent } from './features/profile/profile';
import { PublicHomeComponent } from './pages/public-home/public-home';
export const routes: Routes = [
 
 
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard]
  },
  {
  path: 'forgot-password',
  loadComponent: () =>
    import('./pages/forgot-password/forgot-password')
      .then(m => m.ForgotPasswordComponent)
},
{
  path: 'reset-password',
  loadComponent: () =>
    import('./pages/reset-password/reset-password')
      .then(m => m.ResetPasswordComponent)
}
,
 {
    path: 'profile',
    canActivate: [AuthGuard],
    component: ProfileComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  { path: 'register', component: RegisterComponent },
{
    path: 'admin',
     component: AdminLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    children: [
      { path: '', component: AdminHomeComponent },
       { path: 'users', component: AdminPanelComponent }
    ]
  },
  {
  path: 'agent/queue',
  component: AgentQueueComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['AGENT'] }
},
{
  path: 'tickets/create',
  component: CreateTicketComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['USER'] }
},
{
  path: 'tickets/my',
  component: MyTicketsComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['USER'] }
},
{
  path: 'tickets/:id',
  component: TicketDetailsComponent,
  canActivate: [AuthGuard]
},
{
  path: 'manager/tickets',
  component: ManagerTicketListComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['MANAGER', 'ADMIN'] }
},
{
  path: 'admin/sla',
  component: SlaBreachesComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] }
},
{
  path: 'manager/escalations',
  component: ManagerEscalationsComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['MANAGER'] }
},
{
  path: 'admin/escalations',
  component: AdminEscalationsComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] }
},
{
  path: 'manager/dashboard',
  component: ManagerDashboardComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['MANAGER'] }
},
{
    path: 'reports',
    component: ReportsComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'MANAGER'] }
  }




];
