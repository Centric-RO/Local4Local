import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { MerchantDto } from '../models/merchant-dto.model';
import { InviteMerchantsDto } from '../models/invite-merchants-dto.model';

@Injectable({
    providedIn: 'root'
})
export class MerchantService {

    constructor(private httpClient: HttpClient) { }

    public registerMerchant(merchantDto: MerchantDto): Observable<MerchantDto> {
        return this.httpClient.post<MerchantDto>(`${environment.apiPath}/merchant/register`, merchantDto);
    }

    public getAllMerchants(): Observable<MerchantDto[]> {
        return this.httpClient.get<MerchantDto[]>(`${environment.apiPath}/merchant/all`);
    }

    public getPaginatedMerchants(page: number, size: number): Observable<MerchantDto[]> {
        const httpParams = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());
        return this.httpClient.get<MerchantDto[]>(`${environment.apiPath}/merchant/paginated`,
            {
                params: httpParams,
                withCredentials: true
            });
    }

    public getMerchantsByCategory(categoryId: number): Observable<MerchantDto[]> {
        return this.httpClient.get<MerchantDto[]>(`${environment.apiPath}/merchant/filter/${categoryId}`);
    }

    public inviteMerchants(inviteMerchantsDto: InviteMerchantsDto): Observable<void> {
        return this.httpClient.post<void>(`${environment.apiPath}/merchant/invite`, inviteMerchantsDto, { withCredentials: true });
    }

    public countAllMerchants(): Observable<number> {
        return this.httpClient.get<number>(
            `${environment.apiPath}/merchant/count/all`,
            { withCredentials: true }
        );
    }
}