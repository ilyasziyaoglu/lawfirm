import {Component} from '@angular/core';
import {JobApplicationsService} from "../entities/job-applications/service/job-applications.service";
import {FormControl, FormGroup} from "@angular/forms";
import {JobApplications} from "../entities/job-applications/job-applications.model";

@Component({
  selector: 'jhi-career',
  templateUrl: './career.component.html',
  styleUrls: ['./career.component.scss']
})
export class CareerComponent {

  applyForm = new FormGroup({
    name: new FormControl(''),
    surname: new FormControl(''),
    email: new FormControl(''),
    phone: new FormControl(''),
    area: new FormControl(''),
    message: new FormControl(''),
    cv: new FormControl(new File([], 'cv.pdf')),
    cvContentType: new FormControl(''),
  });

  constructor(
    private jobApplicationsService: JobApplicationsService,
  ) {
  }

  applyForJob(): void {
    this.jobApplicationsService.create(this.applyForm.value as JobApplications).subscribe(() => {
      alert('Başvurunuz başarıyla alınmıştır.');
      this.applyForm.reset();
    });
  }

  onFileChange(event: any): void {
    const reader = new FileReader();
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      this.applyForm.controls.cvContentType.setValue(file.type);
      reader.readAsDataURL(file);
      reader.onload = () => {
        try {
          if (typeof (reader.result) === 'string') {
            this.applyForm.controls.cv.setValue(reader.result.split(',')[1]);
          } else {
            this.applyForm.controls.cv.setValue(reader.result);
          }
        } catch (e) {
          console.error(e);
        }
      };
    }
  }
}
