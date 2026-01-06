export type NotificationType =
  | 'TICKET_CREATED'
  | 'TICKET_ASSIGNED'
  | 'STATUS_CHANGED'
  | 'SLA_BREACH'
  | 'ESCALATION';

export interface Notification {
  id: string;
  userId: string;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  createdAt: string; 
}
