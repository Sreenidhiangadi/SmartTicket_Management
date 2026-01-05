import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    <nav *ngIf="total > size" class="mt-3">
      <ul class="pagination justify-content-center">

        <li class="page-item" [class.disabled]="page === 0">
          <button class="page-link" (click)="change(page - 1)">
            Prev
          </button>
        </li>

        <li
          class="page-item"
          *ngFor="let p of pages"
          [class.active]="p === page"
        >
          <button class="page-link" (click)="change(p)">
            {{ p + 1 }}
          </button>
        </li>

        <li
          class="page-item"
          [class.disabled]="(page + 1) * size >= total"
        >
          <button class="page-link" (click)="change(page + 1)">
            Next
          </button>
        </li>

      </ul>
    </nav>
  `
})
export class PaginationComponent {

  @Input() page = 0;
  @Input() size = 10;
  @Input() total = 0;

  @Output() pageChange = new EventEmitter<number>();

  get pages(): number[] {
    return Array.from(
      { length: Math.ceil(this.total / this.size) },
      (_, i) => i
    );
  }

  change(p: number): void {
    if (p < 0 || p * this.size >= this.total) return;
    this.pageChange.emit(p);
  }
}
