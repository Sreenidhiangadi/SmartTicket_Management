export interface TicketQuery {
  page: number;
  size: number;
  status?: string;
  priority?: string;
}
